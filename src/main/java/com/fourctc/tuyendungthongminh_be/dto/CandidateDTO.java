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
public class CandidateDTO {
    private UUID candidateId;
    private UUID userId;
    private String fullName;
    private String email;
    private String headline;
    private UUID defaultCvId;
    private String profileVisibility;
    private String photoUrl;
    private Timestamp updatedAt;
}