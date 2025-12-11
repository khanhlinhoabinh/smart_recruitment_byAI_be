
package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import com.fourctc.tuyendungthongminh_be.entity.Employer;
import com.fourctc.tuyendungthongminh_be.service.EmployerService;
import com.fourctc.tuyendungthongminh_be.mapper.EmployerMapper;
import com.fourctc.tuyendungthongminh_be.repository.EmployerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.util.List;


@RestController
@RequestMapping("/employers")
public class EmployerController {

    private final EmployerService employerService;
    private final EmployerRepository employerRepository;   // THÊM DÒNG NÀY
    private final EmployerMapper employerMapper;

    public EmployerController(EmployerService employerService,
                              EmployerRepository employerRepository,
                              EmployerMapper employerMapper) {
        this.employerService = employerService;
        this.employerRepository = employerRepository;
        this.employerMapper = employerMapper;
    }

    // HR & ADMIN xem hồ sơ employer theo id
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployerDTO> getEmployer(@PathVariable("id") UUID employerId) {
        // Gọi bản service không kiểm tra owner để ADMIN cũng xem được
        return ResponseEntity.ok(employerService.getEmployer(employerId));
    }

    @PreAuthorize("hasRole('HR')")
    @GetMapping("/me")
    public ResponseEntity<EmployerDTO> getMyEmployer(Principal principal) {
        String currentEmail = principal.getName();
        Employer employer = employerRepository.findByUser_Email(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa tạo hồ sơ Employer"));

        return ResponseEntity.ok(employerMapper.employerEntityToEmployerDTO(employer));
    }
    // ✅ HR tạo employer KHÔNG cần userId trong body — lấy từ token (Principal)
    @PreAuthorize("hasRole('HR')")

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployerDTO> createEmployer(@ModelAttribute EmployerDTO dto,
                                                      @RequestPart(value = "laborContractFile", required = false) MultipartFile laborContractFile,
                                                      Principal principal) {
        // Gán file vào DTO để service xử lý
        dto.setLaborContractFile(laborContractFile);
        EmployerDTO result = employerService.createEmployer(dto, principal);
        return ResponseEntity.ok(result);
    }

    // ✅ HR cập nhật employer (service sẽ kiểm tra owner theo Principal)
    @PreAuthorize("hasRole('HR')")

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployerDTO> updateEmployer(@PathVariable("id") UUID employerId,
                                                      @ModelAttribute EmployerDTO dto,
                                                      @RequestPart(value = "laborContractFile", required = false) MultipartFile laborContractFile,
                                                      Principal principal) {
        dto.setLaborContractFile(laborContractFile);
        EmployerDTO result = employerService.updateEmployer(employerId, dto, principal);
        return ResponseEntity.ok(result);
    }

    // HR bấm nút "Yêu cầu duyệt" — KHÔNG yêu cầu upload GPKD nữa
    @PreAuthorize("hasRole('HR')")
    @PostMapping("/{id}/request-verification")
    public ResponseEntity<EmployerDTO> requestVerification(@PathVariable("id") UUID employerId,
                                                           Principal principal) {
        EmployerDTO result = employerService.requestVerification(employerId, principal);
        return ResponseEntity.ok(result);
    }

    // CHỈ ADMIN được duyệt
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve-verification")
    public ResponseEntity<EmployerDTO> approveVerification(@PathVariable("id") UUID employerId,
                                                           Principal principal) {
        EmployerDTO result = employerService.approveVerification(employerId, principal);
        return ResponseEntity.ok(result);
    }

    // ADMIN từ chối (tùy chọn)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject-verification")
    public ResponseEntity<EmployerDTO> rejectVerification(@PathVariable("id") UUID employerId,
                                                          @RequestBody Map<String, String> body,
                                                          Principal principal) {
        String reason = body != null ? body.get("reason") : null;
        EmployerDTO result = employerService.rejectVerification(employerId, reason, principal);
        return ResponseEntity.ok(result);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-verification")
    public ResponseEntity<List<EmployerDTO>> getPendingVerificationEmployers() {
        List<Employer> pending = employerRepository.findByVerifiedFalse();
        List<EmployerDTO> dtos = pending.stream()
                .map(employerMapper::employerEntityToEmployerDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // EmployerController.java
    @PreAuthorize("hasRole('HR')")
    @GetMapping("/by-company/me")
    public ResponseEntity<List<EmployerDTO>> getMyCompanyEmployers(Principal principal) {
        // Lấy danh sách employer thuộc công ty của HR hiện tại
        return ResponseEntity.ok(employerService.getEmployersByCurrentHrCompany(principal));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<EmployerDTO>> getAllEmployers() {
        List<EmployerDTO> dtos = employerService.getAllEmployers();
        return ResponseEntity.ok(dtos);
    }

}