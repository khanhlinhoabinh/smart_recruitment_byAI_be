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






}
