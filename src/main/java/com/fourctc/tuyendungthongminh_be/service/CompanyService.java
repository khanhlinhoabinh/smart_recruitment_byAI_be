package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.entity.Company;
import com.fourctc.tuyendungthongminh_be.mapper.CompanyMapper;
import com.fourctc.tuyendungthongminh_be.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CompanyService {

    @Autowired private CompanyRepository companyRepository;
    @Autowired private CompanyMapper companyMapper;

    // === PUBLIC: chỉ ACTIVE & APPROVE ===
    public List<CompanyDTO> getActiveCompanies() {
        return companyRepository
                .findByStatusAndVerify(Company.Status.ACTIVE, Company.Verify.APPROVE)
                .stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
    }

    // === ADMIN: xem tất cả (PENDING/APPROVE/REJECT), sắp xếp mới nhất + STT ===
    public List<CompanyDTO> getAllCompanies() {
        List<CompanyDTO> dtos = companyRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
        IntStream.range(0, dtos.size()).forEach(i -> dtos.get(i).setOrderNumber(i + 1));
        return dtos;
    }

    public CompanyDTO createCompany(CompanyDTO dto, String createdBy) {
        // Validate tên + MST
        if (isBlank(dto.getName())) throw new IllegalArgumentException("Tên công ty là bắt buộc");
        if (isBlank(dto.getTaxCode())) throw new IllegalArgumentException("Mã số thuế là bắt buộc");

        if (companyRepository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new IllegalArgumentException("Tên công ty đã tồn tại");
        if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim()))
            throw new IllegalArgumentException("Mã số thuế đã tồn tại");

        Company company = companyMapper.companyDTOToCompanyEntityForCreate(dto);
        company.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        company.setCreatedBy(createdBy);

        // Status (nội bộ) vẫn ACTIVE khi tạo, có thể đổi sau
        company.setStatus(Company.Status.ACTIVE);

        // Verify: phân theo vai trò
        if (hasRole("HR")) {
            company.setVerify(Company.Verify.PENDING);
            // HR bắt buộc GPKD
            if (isBlank(dto.getBusinessRegistrationUrl()) || isBlank(dto.getBusinessRegistrationFileName())) {
                throw new IllegalArgumentException("HR tạo công ty phải đính kèm GPKD (URL và tên file)");
            }
            company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
            company.setBusinessRegistrationFileName(dto.getBusinessRegistrationFileName().trim());
            company.setBusinessRegistrationUploadedAt(new Timestamp(System.currentTimeMillis()));
        } else if (hasRole("ADMIN")) {
            company.setVerify(Company.Verify.APPROVE);
            // Admin: GPKD không bắt buộc, nếu có thì lưu
            if (!isBlank(dto.getBusinessRegistrationUrl())) {
                company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
                company.setBusinessRegistrationFileName(
                        isBlank(dto.getBusinessRegistrationFileName()) ? "N/A" : dto.getBusinessRegistrationFileName().trim()
                );
                company.setBusinessRegistrationUploadedAt(new Timestamp(System.currentTimeMillis()));
            }
        } else {
            throw new SecurityException("Chỉ HR hoặc Admin mới được tạo công ty");
        }

        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    public CompanyDTO updateCompany(UUID id, CompanyDTO dto, String username) {
        Company existing = getCompanyOrThrow(id);

        if (hasRole("HR") && !existing.getCreatedBy().equals(username)) {
            throw new SecurityException("Bạn chỉ có thể sửa công ty do mình tạo.");
        }

        // Map cơ bản
        if (!isBlank(dto.getName())) existing.setName(dto.getName().trim());
        existing.setIndustry(dto.getIndustry());
        existing.setDescription(dto.getDescription());
        existing.setLogoUrl(dto.getLogoUrl());
        existing.setCoverUrl(dto.getCoverUrl());
        existing.setWebsite(dto.getWebsite());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        if (dto.getSize() != null) existing.setSize(Company.CompanySize.valueOf(dto.getSize()));
        existing.setFoundedYear(dto.getFoundedYear());

        // Cho phép đổi status (nội bộ) chỉ Admin
        if (hasRole("ADMIN") && dto.getStatus() != null) {
            existing.setStatus(Company.Status.valueOf(dto.getStatus()));
        }

        // Đổi taxCode (check trùng)
        if (!isBlank(dto.getTaxCode())
                && !dto.getTaxCode().trim().equalsIgnoreCase(existing.getTaxCode())) {
            if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim())) {
                throw new IllegalArgumentException("Mã số thuế đã tồn tại");
            }
            existing.setTaxCode(dto.getTaxCode().trim());
        }

        // Featured: chỉ Admin
        if (hasRole("ADMIN") && dto.getFeatured() != null) {
            existing.setFeatured(dto.getFeatured());
        }

        // Verify: KHÔNG cho đổi trực tiếp qua update; dùng approve/reject endpoint riêng
        Company updated = companyRepository.save(existing);
        return companyMapper.companyEntityToCompanyDTO(updated);
    }

    /** Admin phê duyệt: verify -> APPROVE (public) */
    public CompanyDTO approveCompany(UUID id) {
        Company company = getCompanyOrThrow(id);
        company.setVerify(Company.Verify.APPROVE);
        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    /** Admin từ chối: verify -> REJECT (không public, loại khỏi featured) */
    public CompanyDTO rejectCompany(UUID id) {
        Company company = getCompanyOrThrow(id);
        company.setVerify(Company.Verify.REJECT);
        company.setFeatured(false);
        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    /** Admin xóa */
    public void deleteCompany(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Công ty không tồn tại");
        }
        companyRepository.deleteById(id);
    }

    /** Admin đánh dấu nổi bật: chỉ cho phép khi ACTIVE + APPROVE */
    public CompanyDTO setFeatured(UUID id, boolean featured) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.ACTIVE || company.getVerify() != Company.Verify.APPROVE) {
            throw new IllegalStateException("Chỉ công ty ACTIVE & APPROVE mới được đánh dấu nổi bật");
        }
        company.setFeatured(featured);
        return companyMapper.companyEntityToCompanyDTO(companyRepository.save(company));
    }

    /** Public: chi tiết công ty chỉ khi ACTIVE + APPROVE */
    public CompanyDTO getCompanyById(UUID id) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.ACTIVE || company.getVerify() != Company.Verify.APPROVE) {
            throw new IllegalStateException("Công ty chưa được public");
        }
        return companyMapper.companyEntityToCompanyDTO(company);
    }

    /** Public search: chỉ ACTIVE + APPROVE */
    public List<CompanyDTO> searchCompaniesByName(String name) {
        return companyRepository
                .findByNameContainingIgnoreCaseAndStatusAndVerify(
                        name, Company.Status.ACTIVE, Company.Verify.APPROVE)
                .stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
    }

    /** Admin/HR: xem chi tiết không ràng buộc verify/status */
    public CompanyDTO getCompanyByIdAdmin(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Công ty không tồn tại"));
        return companyMapper.companyEntityToCompanyDTO(company);
    }

    private Company getCompanyOrThrow(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Công ty không tồn tại"));
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}