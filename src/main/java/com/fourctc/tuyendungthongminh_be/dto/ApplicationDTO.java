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
public class ApplicationDTO {
    private UUID applicationId;

    private UUID jobId;
    private String jobTitle;

    private UUID candidateId;
    private String candidateName;
    private String email;

    private UUID cvId;
    private String cvTitle;

    private String status;
    private String notes;
    private Timestamp appliedAt;
}