package com.fourctc.tuyendungthongminh_be.controller;


import com.fourctc.tuyendungthongminh_be.dto.LoginRequest;
import com.fourctc.tuyendungthongminh_be.dto.LoginResponse;
import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import com.fourctc.tuyendungthongminh_be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    // API đăng ký người dùng mới
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.registerUser(userDTO);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Lỗi máy chủ: " + ex.getMessage());
        }
    }
    // API đăng nhập trả về Access Token và Refresh Token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Lỗi máy chủ: " + ex.getMessage());
        }
    }
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email không được để trống");
            }
          userService.requestPasswordReset(email);
            return ResponseEntity.ok("Đã gửi email reset mật khẩu");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Lỗi máy chủ: " + ex.getMessage());
        }
    }

    /**
     * API xác minh email
     * Endpoint: GET /users/verify?token=...
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String result = userService.verifyEmail(token);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Lỗi máy chủ: " + ex.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = userService.validateResetToken(token);

        if (!isValid) {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn");
        }

        return ResponseEntity.ok("Token hợp lệ");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Không cần xử lý gì ở server nếu không lưu token
        return ResponseEntity.ok("Đăng xuất thành công");
    }
}
