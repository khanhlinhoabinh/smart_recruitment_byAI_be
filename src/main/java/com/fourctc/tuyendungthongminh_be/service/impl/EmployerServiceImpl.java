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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
@Transactional
public class EmployerServiceImpl implements EmployerService {
    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;
    private final UserRepository userRepository;
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
    public EmployerDTO getEmployer(UUID employerId) {
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));
        return employerMapper.employerEntityToEmployerDTO(employer);
    }

    @Override
    public EmployerDTO requestVerification(UUID employerId, Principal principal) {
        String currentEmail = resolveCurrentUserEmail(principal);
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));
        // Quyền sở hữu
        if (employer.getUser() == null || !employer.getUser().getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("Bạn không có quyền thực hiện hành động này");
        }
        // Tuỳ chọn: vẫn yêu cầu đã gắn Company; nếu muốn bỏ luôn thì xoá khối này
        if (employer.getCompany() == null || employer.getCompany().getCompanyId() == null) {
            throw new IllegalStateException("Vui lòng chọn công ty trước khi gửi yêu cầu duyệt");
        }
        return employerMapper.employerEntityToEmployerDTO(employer);
    }

    @Override
    public EmployerDTO approveVerification(UUID employerId, Principal principal) {
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));

        // Chỉ admin mới vào được đây (do @PreAuthorize ở controller)

        User admin = getCurrentUser(resolveCurrentUserEmail(principal));

        employer.setVerified(true);
        employer.setVerifiedAt(java.sql.Timestamp.from(java.time.Instant.now()));
        // Nếu muốn biết ai duyệt thì thêm cột verified_by sau, tạm để vậy đã

        employerRepository.save(employer);
        return employerMapper.employerEntityToEmployerDTO(employer);
    }

    @Override
    public EmployerDTO rejectVerification(UUID employerId, String reason, Principal principal) {
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer không tồn tại"));

        employer.setVerified(false);
        employer.setVerifiedAt(null);
        // Có thể thêm cột rejected_reason nếu cần

        employerRepository.save(employer);
        return employerMapper.employerEntityToEmployerDTO(employer);
    }

    // EmployerServiceImpl.java
    @Override
    public List<EmployerDTO> getEmployersByCurrentHrCompany(Principal principal) {
        String currentEmail = resolveCurrentUserEmail(principal);
        // Lấy employer của chính user hiện tại
        Employer me = employerRepository.findByUser_Email(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa tạo hồ sơ Employer"));
        Company company = me.getCompany();
        if (company == null || company.getCompanyId() == null) {
            throw new IllegalStateException("Bạn chưa gắn vào công ty nào");
        }
        // Lấy tất cả employer thuộc công ty này
        List<Employer> list = employerRepository.findByCompany_CompanyId(company.getCompanyId());
        return list.stream()
                .map(employerMapper::employerEntityToEmployerDTO)
                .toList();
    }
}