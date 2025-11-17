package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.SavedJobDTO;
import com.fourctc.tuyendungthongminh_be.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping
    public String saveJob(@RequestParam UUID userId, @RequestParam UUID jobId) {
        savedJobService.saveJob(userId, jobId);
        return "Job saved successfully!";
    }

    @GetMapping
    public List<SavedJobDTO> getSavedJobs(@RequestParam UUID userId) {
        return savedJobService.getSavedJobs(userId);
    }

    @DeleteMapping("/{jobId}")
    public String deleteSavedJob(@RequestParam UUID userId, @PathVariable UUID jobId) {
        savedJobService.deleteSavedJob(userId, jobId);
        return "Job removed successfully!";
    }
}
