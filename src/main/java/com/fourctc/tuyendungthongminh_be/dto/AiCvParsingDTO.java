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
public class AiCvParsingDTO {
    private UUID parseId;

    private UUID cvId;
    private String cvTitle;
    private UUID candidateId;
    private String candidateName;

    private String skills;      // JSON string
    private String experience;  // JSON string
    private String education;   // JSON string
    private String summary;

    private Timestamp createdAt;
}