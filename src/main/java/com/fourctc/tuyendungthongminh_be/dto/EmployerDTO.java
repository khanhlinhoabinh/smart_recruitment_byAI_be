package com.fourctc.tuyendungthongminh_be.dto;

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
}