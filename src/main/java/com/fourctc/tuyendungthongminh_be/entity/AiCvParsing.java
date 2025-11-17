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
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CV cv;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "job_titles", columnDefinition = "TEXT")
    private String jobTitles;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "education", columnDefinition = "TEXT")
    private String education;

    @Column(name = "locations", columnDefinition = "TEXT")
    private String locations;

    @Column(name = "languages", columnDefinition = "TEXT")
    private String languages;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

}