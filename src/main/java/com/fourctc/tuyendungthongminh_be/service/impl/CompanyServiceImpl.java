
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

    /* ===== CREATE/UPDATE/DELETE ===== */
    @Override
    public CompanyDTO createCompany(CompanyDTO dto, String createdByEmail) {
        // Validate cơ bản
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

        // Phân quyền theo role (giống pattern bạn dùng)
        if (hasRole("HR")) {
            company.setVerify(Company.Verify.PENDING);
            if (isBlank(dto.getBusinessRegistrationUrl()) || isBlank(dto.getBusinessRegistrationFileName())) {
                throw new IllegalArgumentException("HR tạo công ty phải đính kèm GPKD (URL và tên file)");
            }
            company.setBusinessRegistrationUrl(dto.getBusinessRegistrationUrl().trim());
            company.setBusinessRegistrationFileName(dto.getBusinessRegistrationFileName().trim());
            company.setBusinessRegistrationUploadedAt(Timestamp.from(Instant.now()));
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

    /* ===== NEW: HR UPLOAD GPKD & REQUEST VERIFY ===== */

    @Override
    public Map<String, Object> uploadBusinessRegistrationForCompany(UUID companyId,
                                                                    MultipartFile file,
                                                                    Principal principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ĐKKD trống");
        }

        Company company = getCompanyOrThrow(companyId);
        String currentEmail = currentEmail(principal);
        ensureOwner(company.getCreatedBy(), currentEmail);

        // Lưu file local: uploads/company/{companyId}/business_registration-{ts}.{ext}
        String companyIdStr = company.getCompanyId().toString();
        String originalName = Path.of(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
        String storedName = "business_registration-" + Instant.now().toEpochMilli() + ext;

        Path companyDir = Path.of(baseUploadDir, "company", companyIdStr);
        Path storedPath = companyDir.resolve(storedName);
        try {
            Files.createDirectories(companyDir);
            Files.copy(file.getInputStream(), storedPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file ĐKKD: " + e.getMessage(), e);
        }

        // Tạo URL public theo pattern EmployerServiceImpl
        String publicUrl = "/" + baseUploadDir + "/company/" + companyIdStr + "/" + storedName; // serve qua ResourceHandler
        company.setBusinessRegistrationUrl(publicUrl);
        company.setBusinessRegistrationFileName(storedName);
        company.setBusinessRegistrationUploadedAt(Timestamp.from(Instant.now()));
        companyRepository.save(company);

        Map<String, Object> payload = new HashMap<>();
        payload.put("businessRegistrationUrl", publicUrl);
        payload.put("businessRegistrationFileName", storedName);
        payload.put("businessRegistrationUploadedAt", company.getBusinessRegistrationUploadedAt());
        payload.put("companyId", company.getCompanyId());
        return payload;
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
