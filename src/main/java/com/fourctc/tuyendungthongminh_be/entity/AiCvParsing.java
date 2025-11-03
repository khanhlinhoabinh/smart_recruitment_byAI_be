package com.fourctc.tuyendungthongminh_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_cv_parsing")
public class AiCvParsing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "parse_id", nullable = false, unique = true)
    private UUID parseId;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "cv_id")
    private CV cv;

    @Column(name = "skills", columnDefinition = "JSONB")
    private String skills;

    @Column(name = "experience", columnDefinition = "JSONB")
    private String experience;

    @Column(name = "education", columnDefinition = "JSONB")
    private String education;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}