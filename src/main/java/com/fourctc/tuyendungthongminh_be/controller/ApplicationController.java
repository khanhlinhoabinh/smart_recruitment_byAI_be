
package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationStatusDTO;
import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.dto.ApplicationRequest;
import com.fourctc.tuyendungthongminh_be.entity.Application;
import com.fourctc.tuyendungthongminh_be.mapper.ApplicationMapper;
import com.fourctc.tuyendungthongminh_be.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;

    // ✅ Ứng viên tạo ứng tuyển
    @PostMapping
    public ApplicationDTO createApplication(@RequestBody ApplicationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Application application = applicationService.createApplication(request, userEmail);
        return applicationMapper.applicationEntityToApplicationDTO(application);
    }

    // ✅ Xem danh sách ứng tuyển của ứng viên
    @GetMapping("/my")
    public List<ApplicationDTO> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        return applicationService.getApplicationsForCandidate(userEmail);
    }

    // ✅ HR xem danh sách ứng tuyển theo JobId (đã xử lý status=null)
    @GetMapping("/job/{jobId}")
    public List<ApplicationDTO> getApplicationsByJobId(@PathVariable UUID jobId,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Application.ApplicationStatus statusEnum = null;

        if (status != null && !status.equalsIgnoreCase("null") && !status.isBlank()) {
            try {
                statusEnum = Application.ApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Status không hợp lệ: " + status);
            }
        }

        return applicationService.getApplicationsByJobId(jobId, statusEnum, page, size).getContent();
    }

    // ✅ HR xem chi tiết ứng tuyển
    @GetMapping("/{applicationId}")
    public ApplicationDTO getApplicationDetail(@PathVariable UUID applicationId) {
        return applicationService.getApplicationDetail(applicationId);
    }

    // ✅ HR cập nhật trạng thái ứng tuyển
    @PutMapping("/{applicationId}/status")
    public ApplicationDTO updateApplicationStatus(@PathVariable UUID applicationId,
                                                  @RequestParam Application.ApplicationStatus status) {
        return applicationService.updateApplicationStatus(applicationId, status);
    }

    // ✅ Ứng viên kiểm tra trạng thái ứng tuyển cho một Job
    @GetMapping("/status")
    public ApplicationStatusDTO getMyApplicationStatus(@RequestParam UUID jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return applicationService.getMyApplicationStatus(userEmail, jobId);
    }
}
