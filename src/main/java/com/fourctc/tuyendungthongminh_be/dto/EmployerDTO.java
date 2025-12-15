package com.fourctc.tuyendungthongminh_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDTO {
    private UUID employerId;
    private UUID userId;
    private String fullName;
    private String email;
    private UUID companyId;
    private String companyName;
    private String positionTitle;
    private String department;
    private String workEmail;
    private String phone;
    private boolean verified;
    private Timestamp verifiedAt;

    @JsonIgnore                  // input, không trả về JSON
    private MultipartFile laborContractFile;

    private String laborContractPath; // output: đường dẫn đã lưu để FE thấy
}