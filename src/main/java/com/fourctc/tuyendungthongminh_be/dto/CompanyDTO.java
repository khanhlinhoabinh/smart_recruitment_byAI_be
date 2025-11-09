package com.fourctc.tuyendungthongminh_be.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CompanyDTO {
    private UUID companyId;
    private String name;
    private String industry;
    private String description;
    private String logoUrl;
    private String coverUrl;
    private String website;
    private String address;
    private String city;
    private String size;
    private Integer foundedYear;
    @JsonInclude(JsonInclude.Include.NON_NULL)  // Không trả về nếu null
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean featured;  // Dùng Boolean (có thể null)
    private Timestamp createdAt;
    private String createdBy;
}