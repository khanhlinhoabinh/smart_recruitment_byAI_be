
package com.fourctc.tuyendungthongminh_be.service.impl;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.entity.Company;
import com.fourctc.tuyendungthongminh_be.mapper.CompanyMapper;
import com.fourctc.tuyendungthongminh_be.repository.CompanyRepository;
import com.fourctc.tuyendungthongminh_be.service.CompanyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Value("${app.upload.base-dir:uploads}")
    private String baseUploadDir;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    /* ===== Helpers ===== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String currentEmail(Principal principal) { return principal.getName(); }
    private void ensureOwner(String createdBy, String currentEmail) {
        if (createdBy != null && !createdBy.equals(currentEmail)) {
            throw new AccessDeniedException("Bạn không có quyền thao tác công ty này");
        }
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

    /* ===== PUBLIC ===== */
    @Override
    public List<CompanyDTO> getActiveCompanies() {
        return companyRepository
                .findByStatusAndVerify(Company.Status.ACTIVE, Company.Verify.APPROVE)
                .stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDTO getCompanyById(UUID id) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.ACTIVE || company.getVerify() != Company.Verify.APPROVE) {
            throw new IllegalStateException("Công ty chưa được public");
        }
        return companyMapper.companyEntityToCompanyDTO(company);
    }

    /* ===== ADMIN/HR VIEW ===== */
    @Override
    public List<CompanyDTO> getAllCompanies() {
        List<CompanyDTO> dtos = companyRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
        IntStream.range(0, dtos.size()).forEach(i -> dtos.get(i).setOrderNumber(i + 1));
        return dtos;
    }

    @Override
    public CompanyDTO getCompanyByIdAdmin(UUID id) {
        Company company = getCompanyOrThrow(id);
        return companyMapper.companyEntityToCompanyDTO(company);
    }

    // Sửa createCompany: Xóa bắt buộc URL/fileName cho HR (giờ dùng API mới)
    @Override
    public CompanyDTO createCompany(CompanyDTO dto, String createdByEmail) {
        // Validate cơ bản (giữ nguyên)
        if (isBlank(dto.getName())) throw new IllegalArgumentException("Tên công ty là bắt buộc");
        if (isBlank(dto.getTaxCode())) throw new IllegalArgumentException("Mã số thuế là bắt buộc");
        if (companyRepository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new IllegalArgumentException("Tên công ty đã tồn tại");
        if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim()))
            throw new IllegalArgumentException("Mã số thuế đã tồn tại");

        Company company = companyMapper.companyDTOToCompanyEntityForCreate(dto);
        company.setCreatedAt(Timestamp.from(Instant.now()));
        company.setCreatedBy(createdByEmail);
        company.setStatus(Company.Status.ACTIVE); // nội bộ

        // Phân quyền theo role
        if (hasRole("HR")) {
            company.setVerify(Company.Verify.PENDING);
            // BẮT BUỘC phải có businessRegistrationUrl từ frontend gửi lên
            if (isBlank(dto.getBusinessRegistrationUrl())) {
                throw new IllegalArgumentException("HR phải cung cấp URL Giấy phép kinh doanh");
            }
            company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
            company.setBusinessRegistrationFileName(dto.getBusinessRegistrationFileName()); // optional
            company.setBusinessRegistrationUploadedAt(Timestamp.from(Instant.now()));
            // Xóa phần check bắt buộc URL/fileName (giờ xử lý ở API mới)
        } else if (hasRole("ADMIN")) {
            company.setVerify(Company.Verify.APPROVE);
            if (!isBlank(dto.getBusinessRegistrationUrl())) {
                company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
                company.setBusinessRegistrationFileName(
                        isBlank(dto.getBusinessRegistrationFileName()) ? "N/A" : dto.getBusinessRegistrationFileName().trim()
                );
                company.setBusinessRegistrationUploadedAt(Timestamp.from(Instant.now()));
            }
        } else {
            throw new AccessDeniedException("Chỉ HR hoặc Admin mới được tạo công ty");
        }

        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    // Thêm: Method mới cho HR với upload tích hợp
    @Override
    public CompanyDTO createCompanyForHR(CompanyDTO dto, String createdByEmail) {
        // HR bắt buộc phải có URL giấy phép kinh doanh
        if (isBlank(dto.getBusinessRegistrationUrl())) {
            throw new IllegalArgumentException("HR phải cung cấp URL Giấy phép kinh doanh");
        }
        // Gọi lại hàm chung → tự động set PENDING + validate
        return createCompany(dto, createdByEmail);
    }

    @Override
    public CompanyDTO updateCompany(UUID id, CompanyDTO dto, String username) {
        Company existing = getCompanyOrThrow(id);
        if (hasRole("HR")) {
            ensureOwner(existing.getCreatedBy(), username);
        }

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

        if (dto.getStatus() != null) {
            Company.Status newStatus;
            try {
                newStatus = Company.Status.valueOf(dto.getStatus().trim());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Giá trị status không hợp lệ. Hỗ trợ: ACTIVE, INACTIVE");
            }
            existing.setStatus(newStatus);
        }

        if (!isBlank(dto.getTaxCode()) && !dto.getTaxCode().trim().equalsIgnoreCase(existing.getTaxCode())) {
            if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim())) {
                throw new IllegalArgumentException("Mã số thuế đã tồn tại");
            }
            existing.setTaxCode(dto.getTaxCode().trim());
        }

        if (hasRole("ADMIN") && dto.getFeatured() != null) {
            existing.setFeatured(dto.getFeatured());
        }

        // verify không đổi trực tiếp qua update
        Company updated = companyRepository.save(existing);
        return companyMapper.companyEntityToCompanyDTO(updated);
    }

    @Override
    public CompanyDTO updateCompanyForHR(UUID companyId, CompanyDTO dto, String username) {
        Company existing = getCompanyOrThrow(companyId);
        ensureOwner(existing.getCreatedBy(), username);

        // Nếu chưa có GPKD → bắt buộc phải gửi URL khi update
        if (isBlank(existing.getBusinessRegistrationUrl()) && isBlank(dto.getBusinessRegistrationUrl())) {
            throw new IllegalArgumentException("Công ty chưa có Giấy phép kinh doanh. Vui lòng cung cấp URL GPKD.");
        }

        // Cập nhật các field thông thường
        if (!isBlank(dto.getName()) && !dto.getName().trim().equalsIgnoreCase(existing.getName())) {
            if (companyRepository.existsByNameIgnoreCase(dto.getName().trim())) {
                throw new IllegalArgumentException("Tên công ty đã tồn tại");
            }
            existing.setName(dto.getName().trim());
        }
        existing.setIndustry(dto.getIndustry());
        existing.setDescription(dto.getDescription());
        existing.setLogoUrl(dto.getLogoUrl());
        existing.setCoverUrl(dto.getCoverUrl());
        existing.setWebsite(dto.getWebsite());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        if (dto.getSize() != null) existing.setSize(Company.CompanySize.valueOf(dto.getSize()));
        existing.setFoundedYear(dto.getFoundedYear());

        if (!isBlank(dto.getTaxCode()) && !dto.getTaxCode().trim().equalsIgnoreCase(existing.getTaxCode())) {
            if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim())) {
                throw new IllegalArgumentException("Mã số thuế đã tồn tại");
            }
            existing.setTaxCode(dto.getTaxCode().trim());
        }

        // Cập nhật GPKD nếu có gửi URL mới
        if (!isBlank(dto.getBusinessRegistrationUrl())) {
            existing.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
            existing.setBusinessRegistrationFileName(
                    isBlank(dto.getBusinessRegistrationFileName()) ? "GPKD.pdf" : dto.getBusinessRegistrationFileName().trim()
            );
            existing.setBusinessRegistrationUploadedAt(Timestamp.from(Instant.now()));
        }

        return companyMapper.companyEntityToCompanyDTO(companyRepository.save(existing));
    }
    @Override
    public void deleteCompany(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Công ty không tồn tại");
        }
        companyRepository.deleteById(id);
    }

    /* ===== VERIFY/FEATURED (ADMIN) ===== */
    @Override
    public CompanyDTO approveCompany(UUID id) {
        Company company = getCompanyOrThrow(id);
        company.setVerify(Company.Verify.APPROVE);
        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    @Override
    public CompanyDTO rejectCompany(UUID id) {
        Company company = getCompanyOrThrow(id);
        company.setVerify(Company.Verify.REJECT);
        company.setFeatured(false);
        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    @Override
    public CompanyDTO setFeatured(UUID id, boolean featured) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.ACTIVE || company.getVerify() != Company.Verify.APPROVE) {
            throw new IllegalStateException("Chỉ công ty ACTIVE & APPROVE mới được đánh dấu nổi bật");
        }
        company.setFeatured(featured);
        return companyMapper.companyEntityToCompanyDTO(companyRepository.save(company));
    }


    @Override
    public CompanyDTO requestVerificationForCompany(UUID companyId, Principal principal) {
        Company company = getCompanyOrThrow(companyId);
        String currentEmail = currentEmail(principal);
        ensureOwner(company.getCreatedBy(), currentEmail);

        if (isBlank(company.getBusinessRegistrationUrl()) || isBlank(company.getBusinessRegistrationFileName())) {
            throw new IllegalStateException("Vui lòng upload GPKD (URL & tên file) trước khi yêu cầu duyệt.");
        }
        if (company.getVerify() == Company.Verify.APPROVE) {
            throw new IllegalStateException("Công ty đã được duyệt. Không cần gửi yêu cầu nữa.");
        }

        company.setVerify(Company.Verify.PENDING);
        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }
}
