package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.JobDTO;
import com.fourctc.tuyendungthongminh_be.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // ✅ Candidate xem các job đã được duyệt
    @GetMapping("/approved")
    public ResponseEntity<List<JobDTO>> getApprovedJobs() {
        return ResponseEntity.ok(jobService.getApprovedJobs());
    }
    // ✅ HR, Admin xem tất cả job
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // ✅ HR thêm job (chờ duyệt)
    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody JobDTO dto, Principal principal) {
        jobService.createJob(dto, principal); // gọi service với principal
        return ResponseEntity.ok("Yêu cầu thêm vị trí công việc đã được gửi đến Admin duyệt.");
    }

    // ✅ HR sửa job (chờ duyệt lại)
    @PreAuthorize("hasRole('HR')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateJob(@PathVariable UUID id, @RequestBody JobDTO dto) {
        jobService.updateJob(id, dto);
        return ResponseEntity.ok("Yêu cầu cập nhật vị trí công việc đã được gửi đến Admin duyệt.");
    }

    // ✅ HR xóa job (xóa trực tiếp, không cần duyệt)
    @PreAuthorize("hasRole('HR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok("Đã xóa vị trí công việc thành công.");
    }

    // ✅ Admin duyệt job
    @PutMapping("/{id}/approve")
    public ResponseEntity<JobDTO> approveJob(@PathVariable UUID id, Principal principal) {
        JobDTO approvedJob = jobService.approveJob(id, principal);
        return ResponseEntity.ok(approvedJob);
    }

    // Lấy danh sách 10 job mới nhất theo ngày đăng (createdAt)
    @GetMapping("/latest")
    public ResponseEntity<List<JobDTO>> getLatestJobs() {
        return ResponseEntity.ok(jobService.getLatestJobs());
    }

    // Tìm việc theo từ khóa, vị trí, ngành nghề
    @GetMapping("/search")
    public ResponseEntity<List<JobDTO>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) UUID category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.searchJobs(keyword, location, category, page, size));
    }

    // ✅ Lấy chi tiết job theo ID
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }


}
