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
    private String taxCode;
    private String industry;
    private String description;
    private String logoUrl;
    private String coverUrl;
    private String website;
    private String address;
    private String city;
    private String size;
    private Integer foundedYear;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String verify;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean featured;

    private Timestamp createdAt;
    private String createdBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String businessRegistrationUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String businessRegistrationFileName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp businessRegistrationUploadedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer orderNumber;
}
