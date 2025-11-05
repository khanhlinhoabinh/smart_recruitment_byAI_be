package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import com.fourctc.tuyendungthongminh_be.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employer/dashboard")
    public String employerDashboard() {
        return "Employer Dashboard";
    }
}
