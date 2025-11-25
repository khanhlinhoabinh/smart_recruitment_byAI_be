package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Employer service:
 * - Các method "mới" nhận Principal để lấy userId từ token (không cần client truyền userId).
 * - Các method "cũ" giữ lại để backward-compatible, nhưng nên tránh dùng.
 */
public interface EmployerService {

    // ===== Khuyến nghị dùng (có Principal) =====
    EmployerDTO createEmployer(EmployerDTO dto, Principal principal);

    EmployerDTO updateEmployer(UUID employerId, EmployerDTO dto, Principal principal);
    List<EmployerDTO> getEmployersByCurrentHrCompany(Principal principal);

    String uploadBusinessRegistration(UUID employerId, MultipartFile file, Principal principal);
    /**
     * Lấy thông tin employer theo id và ràng buộc quyền truy cập theo Principal (owner).
     * Nếu muốn cho ADMIN xem tất cả, có thể xử lý ở Controller bằng @PreAuthorize hoặc
     * tách thêm luồng riêng trong service.
     */
    EmployerDTO getEmployer(UUID employerId, Principal principal);

    // ===== Các method cũ (giữ nếu nơi khác đang gọi; nên chuyển dần sang bản có Principal) =====
    EmployerDTO createEmployer(EmployerDTO dto);

    EmployerDTO updateEmployer(UUID employerId, EmployerDTO dto);

    String uploadBusinessRegistration(UUID employerId, MultipartFile file);

    EmployerDTO getEmployer(UUID employerId);

    EmployerDTO requestVerification(UUID employerId, Principal principal);
    EmployerDTO approveVerification(UUID employerId, Principal principal);
    EmployerDTO rejectVerification(UUID employerId, String reason, Principal principal);
}