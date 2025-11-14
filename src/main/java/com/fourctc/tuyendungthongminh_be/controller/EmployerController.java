
package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import com.fourctc.tuyendungthongminh_be.service.EmployerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/employers")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    // HR & ADMIN xem hồ sơ employer theo id
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployerDTO> getEmployer(@PathVariable("id") UUID employerId) {
        // Gọi bản service không kiểm tra owner để ADMIN cũng xem được
        return ResponseEntity.ok(employerService.getEmployer(employerId));
    }
    // ✅ HR tạo employer KHÔNG cần userId trong body — lấy từ token (Principal)
    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public ResponseEntity<EmployerDTO> createEmployer(@RequestBody EmployerDTO dto, Principal principal) {
        EmployerDTO result = employerService.createEmployer(dto, principal);
        return ResponseEntity.ok(result);
    }

    // ✅ HR cập nhật employer (service sẽ kiểm tra owner theo Principal)
    @PreAuthorize("hasRole('HR')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployerDTO> updateEmployer(@PathVariable("id") UUID employerId,
                                                      @RequestBody EmployerDTO dto,
                                                      Principal principal) {
        EmployerDTO result = employerService.updateEmployer(employerId, dto, principal);
        return ResponseEntity.ok(result);
    }

    // ✅ HR upload ĐKKD cho company của employer — không cần userId, service kiểm tra owner
    @PreAuthorize("hasRole('HR')")
    @PostMapping(path = "/{id}/business-registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadBusinessRegistration(@PathVariable("id") UUID employerId,
                                                                          @RequestPart("file") MultipartFile file,
                                                                          Principal principal) {
        String url = employerService.uploadBusinessRegistration(employerId, file, principal);
        return ResponseEntity.ok(Map.of("businessRegistrationUrl", url));
    }


}
