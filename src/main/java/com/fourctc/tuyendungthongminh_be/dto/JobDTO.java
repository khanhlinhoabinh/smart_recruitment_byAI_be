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
public class JobDTO {
    private UUID jobId;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String jobType;
    private Integer salaryMin;
    private Integer salaryMax;
    private Integer experienceRequired;
    private String status;
    private Integer viewsCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp expiredAt;

    private UUID employerId;
    private String employerName;

    private UUID companyId;
    private String companyName;

    private UUID categoryId;
    private String categoryName;

    private UUID approvedByUserId;
    private String approvedByFullName;
    private Timestamp approvedAt;
}