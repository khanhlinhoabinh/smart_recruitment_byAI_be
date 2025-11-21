package com.fourctc.tuyendungthongminh_be.controller;
import org.springframework.http.MediaType;
import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.service.CVService;
import com.fourctc.tuyendungthongminh_be.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv")
@PreAuthorize("hasRole('CANDIDATE')") // TẤT CẢ ENDPOINT TRONG /api/cv/** CHỈ CANDIDATE MỚI VÀO ĐƯỢC
public class CVController {

    @Autowired
    private CVService cvService;

    @Autowired
    private UserService userService;

    // Lấy userId từ token
    private UUID getCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthorized");
        }
        return userService.getUserIdByEmail(auth.getName());
    }

    // TẠO CV TỪ BUILDER
    @PostMapping("/create-with-data")
    public ResponseEntity<CVDTO> createCVWithData(
            @RequestBody CVDTO dto,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        CVDTO saved = cvService.createCVWithAuth(dto, userId);
        return ResponseEntity.ok(saved);
    }

    // LẤY DANH SÁCH CV CỦA TÔI
    @GetMapping("/my")
    public ResponseEntity<List<CVDTO>> getMyCVs(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(cvService.getCVsByUser(userId));
    }

    // RENDER CV ĐỂ XEM TRƯỚC
    @GetMapping("/render/{cvId}")
    public ResponseEntity<Map<String, Object>> renderCV(@PathVariable UUID cvId) {
        Map<String, Object> result = cvService.renderCV(cvId);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/update/{cvId}")
    public ResponseEntity<CVDTO> updateCV(
            @PathVariable UUID cvId,
            @RequestBody CVDTO dto,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        CVDTO saved = cvService.updateCV(cvId, dto, userId);
        return ResponseEntity.ok(saved);
    }

    // UPLOAD FILE CV (PDF/DOC)
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE   // DÒNG QUAN TRỌNG NHẤT
    )

    public ResponseEntity<?> uploadCandidateCV(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File không được để trống");
        }

        UUID userId = getCurrentUserId(authentication);

        // Lưu file vào thư mục uploads/cv
        String uploadDir = System.getProperty("user.dir") + "/uploads/cv/";
        new File(uploadDir).mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        file.transferTo(filePath.toFile());

        String fileUrl = "http://localhost:8080/uploads/cv/" + fileName;

        // Tạo CV trong DB (t
        CVDTO dto = CVDTO.builder()
                .userId(userId)
                .title(file.getOriginalFilename())        // dùng tên file làm title luôn, hoặc để mặc định
                .cvUrl(fileUrl)
                .visibility("PRIVATE")
                .build();

        CVDTO saved = cvService.createCVWithAuth(dto, userId);
        return ResponseEntity.ok(saved);
    }

    // Giữ lại endpoint cũ (nếu FE còn gọi)
    @PostMapping("/create")
    public ResponseEntity<CVDTO> createCV(@RequestBody CVDTO dto) {
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CVDTO>> getCVByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(cvService.getCVsByUser(userId));
    }

    @DeleteMapping("/{cvId}")
    public ResponseEntity<?> deleteCV(@PathVariable UUID cvId) {
        cvService.deleteCV(cvId);
        return ResponseEntity.ok("CV deleted");
    }
}