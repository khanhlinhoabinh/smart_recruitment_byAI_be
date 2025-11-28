package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // PUBLIC: chỉ ACTIVE + APPROVE
    @GetMapping("/public")
    public ResponseEntity<List<CompanyDTO>> getActiveCompanies() {
        return ResponseEntity.ok(companyService.getActiveCompanies());
    }

    // ADMIN: tất cả công ty (đã sắp xếp mới nhất + STT)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    // HR hoặc ADMIN: tạo công ty (HR -> PENDING, Admin -> APPROVE)
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO dto, Principal principal) {
        return ResponseEntity.ok(companyService.createCompany(dto, principal.getName()));
    }

    // HR/ADMIN: cập nhật công ty
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable UUID id,
            @RequestBody CompanyDTO dto,
            Principal principal) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto, principal.getName()));
    }

    // ADMIN: xóa công ty
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Công ty đã được xóa thành công");
    }

    // ADMIN: đánh dấu nổi bật (chỉ ACTIVE + APPROVE)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/featured")
    public ResponseEntity<CompanyDTO> setFeatured(
            @PathVariable UUID id,
            @RequestParam boolean featured) {
        return ResponseEntity.ok(companyService.setFeatured(id, featured));
    }

    // PUBLIC: danh sách công ty nổi bật (ACTIVE + APPROVE)
    @GetMapping("/featured")
    public ResponseEntity<List<CompanyDTO>> getFeaturedCompanies() {
        return ResponseEntity.ok(companyService.getActiveCompanies().stream()
                .filter(dto -> Boolean.TRUE.equals(dto.getFeatured()))
                .collect(Collectors.toList()));
    }

    // PUBLIC: chi tiết công ty (ACTIVE + APPROVE)
    @GetMapping("/public/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    // PUBLIC: search (ACTIVE + APPROVE)
    @GetMapping("/public/search")
    public ResponseEntity<List<CompanyDTO>> searchCompanies(@RequestParam String name) {
        return ResponseEntity.ok(companyService.searchCompaniesByName(name));
    }

    // ADMIN/HR: xem chi tiết (không ràng buộc verify/status)
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyByIdAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompanyByIdAdmin(id));
    }

    // === NEW: ADMIN phê duyệt (PENDING/REJECT -> APPROVE) ===
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<CompanyDTO> approveCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.approveCompany(id));
    }

    // === NEW: ADMIN từ chối (PENDING -> REJECT) ===
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<CompanyDTO> rejectCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.rejectCompany(id));
    }
}