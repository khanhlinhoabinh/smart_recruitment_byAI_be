package com.fourctc.tuyendungthongminh_be.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobDTO {
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String salaryRange;
}