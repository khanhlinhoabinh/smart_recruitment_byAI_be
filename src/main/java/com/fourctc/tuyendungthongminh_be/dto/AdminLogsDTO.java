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
public class AdminLogsDTO {
    private UUID logId;

    private UUID adminId;
    private String adminName;
    private String adminEmail;

    private String actionType;
    private UUID targetId;
    private String targetType;
    private String description;
    private Timestamp createdAt;
}