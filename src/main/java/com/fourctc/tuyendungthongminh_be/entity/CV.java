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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    private String cvUrl;
    private String title;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    @Column(columnDefinition = "JSON")
    private String data; // Lưu dữ liệu người dùng dưới dạng JSON string

    public enum Visibility {
        PUBLIC, PRIVATE
    }
}