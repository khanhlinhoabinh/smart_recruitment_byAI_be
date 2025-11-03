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
public class AiEmailTemplatesDTO {
    private UUID templateId;
    private String templateName;
    private String subject;
    private String body;
    private Timestamp createdAt;
}