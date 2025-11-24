
package com.fourctc.tuyendungthongminh_be.controller;

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

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;

    @PostMapping
    public ApplicationDTO createApplication(@RequestBody ApplicationRequest request) {
        // Lấy email từ JWT thông qua SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // username trong JWT = email

        Application application = applicationService.createApplication(request, userEmail);
        return applicationMapper.applicationEntityToApplicationDTO(application);
    }


    @GetMapping("/my")
    public List<ApplicationDTO> getMyApplications() {
        // Lấy email từ JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<Application> applications = applicationService.getApplicationsForCandidate(userEmail);
        return applications.stream()
                .map(applicationMapper::applicationEntityToApplicationDTO)
                .toList();
    }

}
