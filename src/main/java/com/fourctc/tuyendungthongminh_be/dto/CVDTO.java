package com.fourctc.tuyendungthongminh_be.dto;

import lombok.*;
import java.sql.Timestamp;
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

    // 📌 NEW: thêm templateId để render layout
    private UUID templateId;
}