package com.fourctc.tuyendungthongminh_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private boolean isVerified;
    private String role;
    private String status;
    private Timestamp createdAt;
    // Mật khẩu người dùng nhập khi đăng ký — chỉ nhận từ client, không trả ra ngoài
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}