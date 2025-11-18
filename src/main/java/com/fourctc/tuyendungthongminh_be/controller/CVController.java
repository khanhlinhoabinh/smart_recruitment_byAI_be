package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.service.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv")
public class CVController {

    @Autowired
    private CVService cvService;

    // ==========================
    // CREATE CV
    // ==========================
    @PostMapping("/create")
    public ResponseEntity<CVDTO> createCV(@RequestBody CVDTO dto) {
        return ResponseEntity.ok(cvService.createCV(dto));
    }

    // ==========================
    // GET CV BY USER
    // ==========================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CVDTO>> getCVByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(cvService.getCVsByUser(userId));
    }

    // ==========================
    // DELETE CV
    // ==========================
    @DeleteMapping("/{cvId}")
    public ResponseEntity<?> deleteCV(@PathVariable UUID cvId) {
        cvService.deleteCV(cvId);
        return ResponseEntity.ok("CV deleted");
    }


    // =====================================================
    // 🚀 UPLOAD CV — CHỈ Candidate mới được phép
    // =====================================================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCandidateCV(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        // Check ROLE
        boolean isCandidate = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CANDIDATE")
                        || a.getAuthority().equals("ROLE_ROLE_CANDIDATE"));

        if (!isCandidate) {
            return ResponseEntity.status(403).body("Only candidates can upload CV");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        // ============================
        // FIX ABSOLUTE PATH
        // ============================
        String uploadDir = System.getProperty("user.dir") + "/uploads/cv/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File destination = new File(uploadDir + fileName);
        file.transferTo(destination);

        // Public URL for frontend
        String fileUrl = "http://localhost:8080/uploads/cv/" + fileName;

        return ResponseEntity.ok(fileUrl);
    }
}