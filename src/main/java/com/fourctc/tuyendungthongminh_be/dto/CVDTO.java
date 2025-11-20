package com.fourctc.tuyendungthongminh_be.dto;

import lombok.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CVDTO {

    private UUID id;
    private UUID userId;
    private String cvUrl;
    private String title;
    private String visibility;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private UUID templateId;

    @Builder.Default
    private Map<String, Object> data = new HashMap<>();
}