package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

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

    /**
     * API xác minh email
     * Endpoint: GET /users/verify?token=...
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String result = userService.verifyEmail(token);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Lỗi máy chủ: " + ex.getMessage());
        }
    }


}
