
package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.dto.ApplicationRequest;
import com.fourctc.tuyendungthongminh_be.entity.Application;
import com.fourctc.tuyendungthongminh_be.mapper.ApplicationMapper;
import com.fourctc.tuyendungthongminh_be.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;

    // ✅ Ứng viên "ứng tuyển / cập nhật" (Upsert)
    @PostMapping
    public ResponseEntity<ApplicationDTO> upsertApplication(@Valid @RequestBody ApplicationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Application application = applicationService.upsertApplication(request, userEmail);
        return ResponseEntity.ok(applicationMapper.applicationEntityToApplicationDTO(application));
    }

    // ✅ Xem danh sách ứng tuyển của chính ứng viên
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<ApplicationDTO> result = applicationService.getApplicationsForCandidate(userEmail);
        return ResponseEntity.ok(result);
    }

    // ✅ HR xem danh sách ứng tuyển theo JobId (status tùy chọn, paging)
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobId(@PathVariable UUID jobId,
                                                                       @RequestParam(required = false) String status,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        Application.ApplicationStatus statusEnum = null;

        if (status != null && !status.equalsIgnoreCase("null") && !status.isBlank()) {
            try {
                statusEnum = Application.ApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Có thể trả 400 nếu muốn strict
                System.out.println("⚠ Status không hợp lệ: " + status);
            }
        }

        List<ApplicationDTO> list = applicationService.getApplicationsByJobId(jobId, statusEnum, page, size).getContent();
        return ResponseEntity.ok(list);
    }

    // ✅ HR xem chi tiết ứng tuyển
    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationDTO> getApplicationDetail(@PathVariable UUID applicationId) {
        ApplicationDTO dto = applicationService.getApplicationDetail(applicationId);
        return ResponseEntity.ok(dto);
    }

    // ✅ HR cập nhật trạng thái ứng tuyển
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(@PathVariable UUID applicationId,
                                                                  @RequestParam Application.ApplicationStatus status) {
        ApplicationDTO dto = applicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.ok(dto);
    }

    // ✅ FRONTEND tiện dụng: kiểm tra đã ứng tuyển chưa (để disable nút hoặc hiển thị "Cập nhật hồ sơ")
    // GET /api/applications/status?jobId=...
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> hasApplied(@RequestParam UUID jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        boolean applied = applicationService
                .getApplicationsForCandidate(userEmail)
                .stream()
                .anyMatch(a -> jobId.equals(a.getJobId()));

        return ResponseEntity.ok(Map.of("applied", applied));
    }

    // ✅ FRONTEND tiện dụng: lấy đơn hiện có theo job để prefill ApplyForm
    // GET /api/applications/by-job/{jobId}
    @GetMapping("/by-job/{jobId}")
    public ResponseEntity<ApplicationDTO> getMyApplicationByJob(@PathVariable UUID jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Service có thể expose method getByCandidateJob(...) trả Optional<Application>
        return applicationService.getApplicationsForCandidate(userEmail).stream()
                .filter(a -> jobId.equals(a.getJobId()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
