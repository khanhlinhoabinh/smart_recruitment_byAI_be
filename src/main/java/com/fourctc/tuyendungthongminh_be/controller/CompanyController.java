package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // CANDIDATE: Xem danh sách công ty HOẠT ĐỘNG
    @GetMapping("/public")
    public ResponseEntity<List<CompanyDTO>> getActiveCompanies() {
        return ResponseEntity.ok(companyService.getActiveCompanies());
    }

    // ADMIN: Xem tất cả công ty
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO dto, Principal principal) {
        return ResponseEntity.ok(companyService.createCompany(dto, principal.getName()));
    }

    // ADMIN/HR: Cập nhật công ty
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable UUID id,
            @RequestBody CompanyDTO dto,  // ← DÙNG DTO MỚI
            Principal principal) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto, principal.getName()));
    }

    // ADMIN: Xóa công ty
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Công ty đã được xóa thành công");
    }

    // ADMIN: Đánh dấu nổi bật
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/featured")
    public ResponseEntity<CompanyDTO> setFeatured(
            @PathVariable UUID id,
            @RequestParam boolean featured) {
        return ResponseEntity.ok(companyService.setFeatured(id, featured));
    }
    // ADMIN: Duyệt công ty
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<CompanyDTO> approveCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.approveCompany(id));
    }

    // CANDIDATE: Xem công ty nổi bật
    @GetMapping("/featured")
    public ResponseEntity<List<CompanyDTO>> getFeaturedCompanies() {
        return ResponseEntity.ok(companyService.getActiveCompanies().stream()
                .filter(CompanyDTO::getFeatured)
                .collect(Collectors.toList()));
    }
    // CANDIDATE: Xem chi tiết công ty
    @GetMapping("/public/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }
}