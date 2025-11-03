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
public class JobCategoryDTO {
    private UUID categoryId;
    private String name;
    private String description;
    private UUID parentCategoryId;
    private String parentCategoryName;
    private Timestamp createdAt;
}