package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.entity.Company;
import com.fourctc.tuyendungthongminh_be.mapper.CompanyMapper;
import com.fourctc.tuyendungthongminh_be.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    // CANDIDATE: Xem danh sách công ty HOẠT ĐỘNG
    public List<CompanyDTO> getActiveCompanies() {
        return companyRepository.findByStatus(Company.Status.ACTIVE).stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
    }

    // ADMIN: Xem tất cả công ty (kể cả chờ duyệt)
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
    }

    public CompanyDTO createCompany(CompanyDTO dto, String createdBy) {
        // 1) Bắt buộc name và taxCode
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên công ty là bắt buộc");
        }
        if (dto.getTaxCode() == null || dto.getTaxCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã số thuế là bắt buộc");
        }

        // 2) Kiểm tra trùng tên và trùng mã số thuế
        if (companyRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new IllegalArgumentException("Tên công ty đã tồn tại");
        }
        if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim())) {
            throw new IllegalArgumentException("Mã số thuế đã tồn tại");
        }

        // 3) Map từ DTO -> Entity (GPKD sẽ set trong logic phía dưới)
        Company company = companyMapper.companyDTOToCompanyEntityForCreate(dto);

        // 4) Set các trường mặc định
        company.setStatus(Company.Status.ACTIVE);           // giữ nguyên behavior hiện tại
        company.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        company.setCreatedBy(createdBy);

        // 5) Role-based: HR bắt buộc có GPKD, Admin thì không bắt buộc
        if (hasRole("HR")) {
            if (isBlank(dto.getBusinessRegistrationUrl()) || isBlank(dto.getBusinessRegistrationFileName())) {
                throw new IllegalArgumentException("HR tạo công ty phải đính kèm GPKD (URL và tên file)");
            }
            company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
            company.setBusinessRegistrationFileName(dto.getBusinessRegistrationFileName().trim());
            company.setBusinessRegistrationUploadedAt(new Timestamp(System.currentTimeMillis()));
        } else if (hasRole("ADMIN")) {
            // Admin không bắt buộc, nhưng nếu có dữ liệu vẫn lưu
            if (!isBlank(dto.getBusinessRegistrationUrl())) {
                company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
                company.setBusinessRegistrationFileName(isBlank(dto.getBusinessRegistrationFileName())
                        ? "N/A" : dto.getBusinessRegistrationFileName().trim());
                company.setBusinessRegistrationUploadedAt(new Timestamp(System.currentTimeMillis()));
            }
        }

        // 6) Lưu
        Company saved = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }


    public CompanyDTO updateCompany(UUID id, CompanyDTO dto, String username) {
        Company existing = getCompanyOrThrow(id);

        if (hasRole("HR") && !existing.getCreatedBy().equals(username)) {
            throw new SecurityException("Bạn chỉ có thể sửa công ty do mình tạo.");
        }

        // Map các field cơ bản
        existing.setName(dto.getName());
        existing.setIndustry(dto.getIndustry());
        existing.setDescription(dto.getDescription());
        existing.setLogoUrl(dto.getLogoUrl());
        existing.setCoverUrl(dto.getCoverUrl());
        existing.setWebsite(dto.getWebsite());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setSize(Company.CompanySize.valueOf(dto.getSize()));
        existing.setFoundedYear(dto.getFoundedYear());

        // CHO PHÉP ĐỔI MÃ SỐ THUẾ (nếu gửi lên) và kiểm tra trùng
        if (dto.getTaxCode() != null && !dto.getTaxCode().trim().isEmpty()
                && !dto.getTaxCode().trim().equalsIgnoreCase(existing.getTaxCode())) {
            if (companyRepository.existsByTaxCodeIgnoreCase(dto.getTaxCode().trim())) {
                throw new IllegalArgumentException("Mã số thuế đã tồn tại");
            }
            existing.setTaxCode(dto.getTaxCode().trim());
        }
        // ADMIN mới được sửa featured (giữ nguyên)
        if (hasRole("ADMIN")) {
            if (dto.getFeatured() != null) {
                existing.setFeatured(dto.getFeatured());
            }
        }
        Company updated = companyRepository.save(existing);
        return companyMapper.companyEntityToCompanyDTO(updated);
    }

    // ADMIN: Xóa công ty
    public void deleteCompany(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Công ty không tồn tại");
        }
        companyRepository.deleteById(id);
    }

    // ADMIN: Đánh dấu công ty nổi bật
    public CompanyDTO setFeatured(UUID id, boolean featured) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.ACTIVE) {
            throw new IllegalStateException("Chỉ công ty HOẠT ĐỘNG mới được đánh dấu nổi bật");
        }
        company.setFeatured(featured);
        return companyMapper.companyEntityToCompanyDTO(companyRepository.save(company));
    }

    private Company getCompanyOrThrow(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Công ty không tồn tại"));
    }

    // Helper để kiểm tra role (dùng trong service)
    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
    public CompanyDTO getCompanyById(UUID id) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.ACTIVE) {
            throw new IllegalStateException("Công ty chưa được kích hoạt");
        }
        return companyMapper.companyEntityToCompanyDTO(company);
    }
    public List<CompanyDTO> searchCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCaseAndStatus(name, Company.Status.ACTIVE)
                .stream()
                .map(companyMapper::companyEntityToCompanyDTO)
                .collect(Collectors.toList());
    }


    public CompanyDTO getCompanyByIdAdmin(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Công ty không tồn tại"));
        // KHÔNG kiểm tra status; admin/HR được xem toàn bộ
        return companyMapper.companyEntityToCompanyDTO(company);
    }

}