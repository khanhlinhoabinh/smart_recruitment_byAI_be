package com.fourctc.tuyendungthongminh_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CV {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;
    private String cvUrl;
    private String title;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 📌 NEW: Thêm quan hệ với Template
    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    public enum Visibility {
        PUBLIC, PRIVATE
    }
}