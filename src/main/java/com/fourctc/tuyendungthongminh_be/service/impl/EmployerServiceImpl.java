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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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


    /**
     * Lưu file hợp đồng lao động vào thư mục local và trả về path tương đối để lưu DB.
     * - Chỉ cho phép image/* hoặc application/pdf
     * - Kích thước tối đa 10MB
     * - Tên file cố định theo employerId + đuôi (giúp overwrite khi update)
     *
     * // NOTE: Nếu muốn lưu lên S3/Cloudinary, thay đoạn này bằng StorageService (tách riêng).
     */

    private String saveLaborContractFile(MultipartFile file, UUID employerId) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            String contentType = file.getContentType();
            if (contentType == null ||
                    !(contentType.startsWith("image/") || contentType.equals("application/pdf"))) {
                throw new IllegalArgumentException("Chỉ cho phép upload ảnh (.png/.jpg) hoặc PDF");
            }
            long maxBytes = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxBytes) {
                throw new IllegalArgumentException("File quá lớn, tối đa 10MB");
            }

            // ===== Base directory tuyệt đối =====
            // Nếu bạn có cấu hình qua application.yml, inject bằng @Value("${app.storage.base-dir}")
            // Ở đây fallback lấy thư mục chạy app (user.dir)
            String baseDir = System.getProperty("user.dir"); // ví dụ: C:\work\smart-recruitment
            // Bạn có thể đổi sang thư mục riêng, ví dụ "C:/data/smart-recruitment"
            // baseDir = "C:/data/smart-recruitment";

            // Tạo đường dẫn tuyệt đối: <baseDir>/uploads/labor-contracts
            Path dirPath = Paths.get(baseDir, "uploads", "labor-contracts");

            // Xác định đuôi file
            String originalName = file.getOriginalFilename();
            String ext = ".dat";
            if (originalName != null && originalName.lastIndexOf('.') >= 0) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            } else if ("application/pdf".equals(contentType)) {
                ext = ".pdf";
            } else if (contentType.startsWith("image/")) {
                ext = ".png"; // fallback
            }

            String fileName = employerId + ext; // overwrite khi update
            Path filePath = dirPath.resolve(fileName);

            // ===== Đảm bảo parent folder tồn tại ngay tại filePath =====
            Files.createDirectories(filePath.getParent());

            // ===== Ghi file ổn định cho Windows/Tomcat embedded =====
            // Thay vì MultipartFile#transferTo, dùng Files.copy để tránh lỗi "path not found"
            try (var in = file.getInputStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về path tương đối để lưu DB (FE có thể hiển thị)
            // Nếu muốn trả về URL public, bạn có thể map sang URL ở Controller/Service khác
            String relativePath = "uploads/labor-contracts/" + fileName;
            System.out.println("[SAVE] Labor contract stored at: " + filePath.toAbsolutePath());
            return relativePath;

        } catch (Exception e) {
            // Log chi tiết để debug
            e.printStackTrace();
            throw new RuntimeException("Upload file thất bại: " + e.getMessage(), e);
        }
    }



// ==================== CREATE ====================

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
        employer.setPhone(currentUser.getPhone());

        if (dto.getCompanyId() != null) {
            employer.setCompany(Company.builder().companyId(dto.getCompanyId()).build());
        }

        if (employer.getWorkEmail() != null &&
                employerRepository.existsByWorkEmail(employer.getWorkEmail())) {
            throw new IllegalArgumentException("Work email đã tồn tại");
        }

        // Lưu trước để có employerId
        Employer saved = employerRepository.save(employer);

        // ===== Upload file & cập nhật path vào DB =====
        MultipartFile laborFile = dto.getLaborContractFile(); // NOTE: Controller đã gán từ @RequestPart
        if (laborFile != null && !laborFile.isEmpty()) {
            System.out.println("[CREATE] Upload labor file: name=" + laborFile.getOriginalFilename()
                    + ", size=" + laborFile.getSize()
                    + ", type=" + laborFile.getContentType());
            String filePath = saveLaborContractFile(laborFile, saved.getEmployerId());
            saved.setLaborContractPath(filePath);
            employerRepository.save(saved); // cập nhật path
        }

        // Lấy lại entity đầy đủ (có user/company) để map DTO
        Employer detail = employerRepository.findByEmployerId(saved.getEmployerId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Employer sau khi lưu"));
        EmployerDTO result = employerMapper.employerEntityToEmployerDTO(detail);

        // NOTE: nếu DTO đã có field laborContractPath, FE sẽ thấy path ở đây.
        return result;
    }

    // ==================== UPDATE ====================

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
        existing.setPhone(currentUser.getPhone());

        if (existing.getWorkEmail() != null &&
                employerRepository.existsByWorkEmailAndEmployerIdNot(existing.getWorkEmail(), employerId)) {
            throw new IllegalArgumentException("Work email đã được dùng bởi employer khác");
        }

        if (dto.getCompanyId() != null) {
            existing.setCompany(Company.builder().companyId(dto.getCompanyId()).build());
        }

        // ===== Upload file (nếu có) & cập nhật path =====
        MultipartFile laborFile = dto.getLaborContractFile(); // NOTE: Controller đã gán từ @RequestPart
        if (laborFile != null && !laborFile.isEmpty()) {
            System.out.println("[UPDATE] Upload labor file: name=" + laborFile.getOriginalFilename()
                    + ", size=" + laborFile.getSize()
                    + ", type=" + laborFile.getContentType());
            String filePath = saveLaborContractFile(laborFile, employerId);
            existing.setLaborContractPath(filePath);
        }

        Employer saved = employerRepository.save(existing);
        return employerMapper.employerEntityToEmployerDTO(saved);
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