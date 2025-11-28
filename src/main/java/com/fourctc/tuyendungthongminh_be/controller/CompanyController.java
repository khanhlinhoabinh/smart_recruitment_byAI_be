
package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.service.CompanyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /* ===== PUBLIC ===== */

    // Danh sách company public: chỉ ACTIVE + APPROVE
    @GetMapping("/public")
    public ResponseEntity<List<CompanyDTO>> getActiveCompanies() {
        return ResponseEntity.ok(companyService.getActiveCompanies());
    }

    // Chi tiết company public: ACTIVE + APPROVE
    @GetMapping("/public/{id}")
    public ResponseEntity<CompanyDTO> getCompanyPublicById(@PathVariable("id") UUID companyId) {
        return ResponseEntity.ok(companyService.getCompanyById(companyId));
    }

    // Featured (public) - lọc từ ACTIVE + APPROVE
    @GetMapping("/featured")
    public ResponseEntity<List<CompanyDTO>> getFeaturedCompanies() {
        List<CompanyDTO> actives = companyService.getActiveCompanies();
        actives.removeIf(c -> c.getFeatured() == null || !c.getFeatured());
        return ResponseEntity.ok(actives);
    }

    /* ===== ADMIN/HR VIEW ===== */

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyByIdAdmin(@PathVariable("id") UUID companyId) {
        return ResponseEntity.ok(companyService.getCompanyByIdAdmin(companyId));
    }

    /* ===== CRUD ===== */

    // Tạo company – ADMIN/HR (HR: verify=PENDING & bắt buộc GPKD)
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO dto, Principal principal) {
        CompanyDTO result = companyService.createCompany(dto, principal.getName());
        return ResponseEntity.ok(result);
    }

    // Cập nhật company – ADMIN/HR (HR chỉ sửa company do mình tạo)
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable("id") UUID companyId,
                                                    @RequestBody CompanyDTO dto,
                                                    Principal principal) {
        CompanyDTO result = companyService.updateCompany(companyId, dto, principal.getName());
        return ResponseEntity.ok(result);
    }

    // Xoá company – chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") UUID companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }

    /* ===== VERIFY (ADMIN) ===== */

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<CompanyDTO> approveCompany(@PathVariable("id") UUID companyId) {
        return ResponseEntity.ok(companyService.approveCompany(companyId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<CompanyDTO> rejectCompany(@PathVariable("id") UUID companyId) {
        return ResponseEntity.ok(companyService.rejectCompany(companyId));
    }

    /* ===== FEATURED (ADMIN) ===== */

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/featured")
    public ResponseEntity<CompanyDTO> setFeatured(@PathVariable("id") UUID companyId,
                                                  @RequestParam("featured") boolean featured) {
        return ResponseEntity.ok(companyService.setFeatured(companyId, featured));
    }

    /* ===== NEW: HR UPLOAD GPKD & REQUEST VERIFY ===== */

    // HR upload GPKD cho company — service kiểm tra owner (createdBy == principal)
    @PreAuthorize("hasRole('HR')")
    @PostMapping(path = "/{id}/business-registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadBusinessRegistrationForCompany(@PathVariable("id") UUID companyId,
                                                                                    @RequestPart("file") MultipartFile file,
                                                                                    Principal principal) {
        Map<String, Object> payload = companyService.uploadBusinessRegistrationForCompany(companyId, file, principal);
        return ResponseEntity.ok(payload);
    }

    // HR “Yêu cầu duyệt” -> verify=PENDING (yêu cầu: phải có GPKD)
    @PreAuthorize("hasRole('HR')")
    @PostMapping("/{id}/request-verification")
    public ResponseEntity<CompanyDTO> requestCompanyVerification(@PathVariable("id") UUID companyId,
                                                                 Principal principal) {
        CompanyDTO dto = companyService.requestVerificationForCompany(companyId, principal);
        return ResponseEntity.ok(dto);
    }
}
