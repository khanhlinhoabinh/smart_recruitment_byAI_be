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
public class AiMatchResultsDTO {
    private UUID matchId;

    private UUID cvId;
    private String cvTitle;
    private UUID candidateId;
    private String candidateName;

    private UUID jobId;
    private String jobTitle;

    private Double matchScore;
    private Double cultureScore;
    private String reasonSummary;
    private Timestamp createdAt;
}