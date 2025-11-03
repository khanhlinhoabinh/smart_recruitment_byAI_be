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
public class SystemAlertsDTO {
    private UUID alertId;
    private String type;
    private String message;
    private Timestamp createdAt;
    private Timestamp resolvedAt;
}