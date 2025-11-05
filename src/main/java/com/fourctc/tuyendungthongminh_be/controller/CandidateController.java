package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.CandidateDTO;
import com.fourctc.tuyendungthongminh_be.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PreAuthorize("hasAnyRole('CANDIDATE','ADMIN')")
    @GetMapping("/candidate/profile")
    public String candidateProfile() {
        return "Candidate Profile";
    }

}
