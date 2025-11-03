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
public class CVDTO {
    private UUID cvId;
    private UUID candidateId;
    private String candidateName;
    private String title;
    private String fileUrl;
    private boolean isDefault;
    private String visibility;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}