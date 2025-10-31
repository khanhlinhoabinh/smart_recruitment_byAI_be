package com.fourctc.tuyendungthongminh_be.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private boolean isEmailVerified;
    private String status;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String fullName, String email, boolean isEmailVerified,
                   String status, String roleName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.status = status;
        this.roleName = roleName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    // (Bạn có thể dùng Lombok để tự động tạo nếu muốn)
}
