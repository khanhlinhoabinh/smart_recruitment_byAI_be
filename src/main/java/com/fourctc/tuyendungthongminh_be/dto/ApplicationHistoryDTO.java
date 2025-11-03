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
public class ApplicationHistoryDTO {
    private UUID historyId;
    private UUID applicationId;
    private String oldStatus;
    private String newStatus;
    private UUID changedByUserId;
    private String changedByFullName;
    private Timestamp changedAt;
}