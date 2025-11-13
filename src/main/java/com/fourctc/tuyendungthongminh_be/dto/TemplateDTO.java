package com.fourctc.tuyendungthongminh_be.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateDTO {

    private UUID id;
    private String name;
    private String previewImage;
    private String htmlLayout;
}