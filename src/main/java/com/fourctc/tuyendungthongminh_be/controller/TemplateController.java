package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.TemplateDTO;
import com.fourctc.tuyendungthongminh_be.service.TemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public TemplateDTO create(@RequestBody TemplateDTO dto) {
        return templateService.createTemplate(dto);
    }

    @GetMapping("/list")
    public List<TemplateDTO> list() {
        return templateService.getAll();
    }
}