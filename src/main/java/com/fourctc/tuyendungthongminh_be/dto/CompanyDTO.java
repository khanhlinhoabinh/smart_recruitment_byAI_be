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
    private String status;
    private Timestamp createdAt;
}
