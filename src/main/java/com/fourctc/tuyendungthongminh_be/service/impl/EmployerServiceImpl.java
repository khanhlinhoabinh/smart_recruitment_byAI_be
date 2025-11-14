package com.fourctc.tuyendungthongminh_be.service.impl;
import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import com.fourctc.tuyendungthongminh_be.entity.Company;
import com.fourctc.tuyendungthongminh_be.entity.Employer;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.mapper.EmployerMapper;
import com.fourctc.tuyendungthongminh_be.repository.EmployerRepository;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import com.fourctc.tuyendungthongminh_be.service.EmployerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;
@Service
@Transactional
public class EmployerServiceImpl implements EmployerService {
    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;
    private final UserRepository userRepository;
    @Value("${app.upload.base-dir:uploads}")
    private String baseUploadDir;
    public EmployerServiceImpl(EmployerRepository employerRepository,
                               EmployerMapper employerMapper,
                               UserRepository userRepository) {
        this.employerRepository = employerRepository;
        this.employerMapper = employerMapper;
        this.userRepository = userRepository;
    }
    // ================= Helper =================
    private String resolveCurrentUserEmail(Principal principal) {
        return principal.getName();   // JWT subject = email
    }
    private User getCurrentUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalStateException("User không tồn tại: " + email);
        }
        return user;
    }
    private void ensureOwner(String ownerEmail, String currentEmail) {
        if (ownerEmail != null && !ownerEmail.equals(currentEmail)) {
            throw new AccessDeniedException("Bạn không có quyền thao tác hồ sơ Employer này");
        }
    }
    // ================= CREATE =================
    @Override
    public EmployerDTO createEmployer(EmployerDTO dto, Principal principal) {
        String currentEmail = resolveCurrentUserEmail(principal);
        User currentUser = getCurrentUser(currentEmail);
        // Không cho tạo trùng employer theo user email
        employerRepository.findByUser_Email(currentEmail).ifPresent(e -> {
            throw new IllegalArgumentException("Bạn đã có hồ sơ Employer. Vui lòng dùng API Update.");
        });
        Employer employer = employerMapper.employerDTOToEmployerEntity(dto);
        employer.setUser(currentUser);
        employer.setPhone(currentUser.getPhone()); // Lấy phone từ User tự động
        if (dto.getCompanyId() != null) {
            employer.setCompany(Company.builder().companyId(dto.getCompanyId()).build());
        }
        if (employer.getWorkEmail() != null &&
                employerRepository.existsByWorkEmail(employer.getWorkEmail())) {
            throw new IllegalArgumentException("Work email đã tồn tại");
        }
        Employer saved = employerRepository.save(employer);
        Employer detail = employerRepository.findByEmployerId(saved.getEmployerId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Employer sau khi lưu"));
        return employerMapper.employerEntityToEmployerDTO(detail);
    }
    // ================= UPDATE =================
    @Override
    public EmployerDTO updateEmployer(UUID employerId, EmployerDTO dto, Principal principal) {
        String currentEmail = resolveCurrentUserEmail(principal);
        User currentUser = getCurrentUser(currentEmail);
        Employer existing = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));
        String ownerEmail = existing.getUser() != null ? existing.getUser().getEmail() : null;
        ensureOwner(ownerEmail, currentEmail);
        existing.setPositionTitle(dto.getPositionTitle());
        existing.setDepartment(dto.getDepartment());
        existing.setWorkEmail(dto.getWorkEmail());
        existing.setPhone(currentUser.getPhone()); // Luôn lấy phone từ User
        if (existing.getWorkEmail() != null &&
                employerRepository.existsByWorkEmailAndEmployerIdNot(existing.getWorkEmail(), employerId)) {
            throw new IllegalArgumentException("Work email đã được dùng bởi employer khác");
        }
        if (dto.getCompanyId() != null) {
            existing.setCompany(Company.builder().companyId(dto.getCompanyId()).build());
        }
        Employer saved = employerRepository.save(existing);
        return employerMapper.employerEntityToEmployerDTO(saved);
    }
    // ================= UPLOAD BUSINESS REGISTRATION =================
    @Override
    public String uploadBusinessRegistration(UUID employerId, MultipartFile file, Principal principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ĐKKD trống");
        }
        String currentEmail = resolveCurrentUserEmail(principal);
        User currentUser = getCurrentUser(currentEmail);
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));
        String ownerEmail = employer.getUser() != null ? employer.getUser().getEmail() : null;
        ensureOwner(ownerEmail, currentEmail);
        Company company = employer.getCompany();
        if (company == null || company.getCompanyId() == null) {
            throw new IllegalStateException("Employer chưa gắn Company");
        }
        String companyIdStr = company.getCompanyId().toString();
        String originalName = Path.of(file.getOriginalFilename()).getFileName().toString();
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
        company.setBusinessRegistrationUrl("/" + baseUploadDir + "/company/" + companyIdStr + "/" + storedName);
        company.setBusinessRegistrationFileName(storedName);
        company.setBusinessRegistrationUploadedAt(java.sql.Timestamp.from(Instant.now()));
        return company.getBusinessRegistrationUrl();
    }
    // ================= GET =================
    @Override
    public EmployerDTO getEmployer(UUID employerId, Principal principal) {
        String currentEmail = resolveCurrentUserEmail(principal);
        User currentUser = getCurrentUser(currentEmail);
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));
        String ownerEmail = employer.getUser() != null ? employer.getUser().getEmail() : null;
        ensureOwner(ownerEmail, currentEmail);
        return employerMapper.employerEntityToEmployerDTO(employer);
    }
    // ================= DISABLED LEGACY METHODS =================
    @Override
    public EmployerDTO createEmployer(EmployerDTO dto) {
        throw new UnsupportedOperationException("Hãy dùng createEmployer(dto, principal).");
    }
    @Override
    public EmployerDTO updateEmployer(UUID employerId, EmployerDTO dto) {
        throw new UnsupportedOperationException("Hãy dùng updateEmployer(id, dto, principal).");
    }
    @Override
    public String uploadBusinessRegistration(UUID employerId, MultipartFile file) {
        throw new UnsupportedOperationException("Hãy dùng uploadBusinessRegistration(id, file, principal).");
    }
    @Override
    public EmployerDTO getEmployer(UUID employerId) {
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));
        return employerMapper.employerEntityToEmployerDTO(employer);
    }
}