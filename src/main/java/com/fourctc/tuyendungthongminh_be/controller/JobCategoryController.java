package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.JobCategoryDTO;
import com.fourctc.tuyendungthongminh_be.service.JobCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/job-categories")
public class JobCategoryController {

    @Autowired
    private JobCategoryService jobCategoryService;


}
