package com.fourctc.tuyendungthongminh_be.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCategoryDTO {
    private UUID categoryId;
    private String name;
    private String description;
    private boolean isPopular;
    private String createdBy;
}
