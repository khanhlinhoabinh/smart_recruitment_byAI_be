package com.fourctc.tuyendungthongminh_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String previewImage;  // link hình thumbnail
    private String htmlLayout;    // HTML layout của mẫu CV
}