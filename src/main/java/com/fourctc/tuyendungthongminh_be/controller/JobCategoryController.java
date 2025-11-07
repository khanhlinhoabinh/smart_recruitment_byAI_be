package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.JobCategoryDTO;
import com.fourctc.tuyendungthongminh_be.service.JobCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/job-categories")
public class JobCategoryController {

    @Autowired
    private JobCategoryService jobCategoryService;

    // Candidate xem ngành nghề phổ biến
    @GetMapping("/popular")
    public ResponseEntity<List<JobCategoryDTO>> getPopularCategories() {
        return ResponseEntity.ok(jobCategoryService.getPopularCategories());
    }


}