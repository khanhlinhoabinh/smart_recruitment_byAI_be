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
        Company entity = companyMapper.companyDTOToCompanyEntityForCreate(dto); // DÙNG MAPPER MỚI
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setFeatured(false);

        if (hasRole("ADMIN")) {
            entity.setStatus(Company.Status.ACTIVE);
        } else {
            entity.setStatus(Company.Status.PENDING);
        }

        Company saved = companyRepository.save(entity);
        return companyMapper.companyEntityToCompanyDTO(saved);
    }
    // ADMIN: Duyệt công ty (chuyển PENDING → ACTIVE)
    public CompanyDTO approveCompany(UUID id) {
        Company company = getCompanyOrThrow(id);
        if (company.getStatus() != Company.Status.PENDING) {
            throw new IllegalStateException("Chỉ có thể duyệt công ty đang chờ.");
        }
        company.setStatus(Company.Status.ACTIVE);
        Company updated = companyRepository.save(company);
        return companyMapper.companyEntityToCompanyDTO(updated);
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

        // CHỈ ADMIN được sửa status & featured
        if (hasRole("ADMIN")) {
            if (dto.getStatus() != null) {
                existing.setStatus(Company.Status.valueOf(dto.getStatus()));
            }
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
}