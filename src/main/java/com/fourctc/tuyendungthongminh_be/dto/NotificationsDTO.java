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
public class NotificationsDTO {
    private UUID notificationId;
    private UUID userId;
    private String fullName;
    private String title;
    private String message;
    private String type;
    private String linkUrl;
    private Boolean isRead;
    private Timestamp createdAt;
}