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
@Table(name = "ai_match_results")
public class AiMatchResults {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "match_id", nullable = false, unique = true)
    private UUID matchId;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CV cv;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    private Job job;

    @Column(name = "match_score", nullable = false)
    private Double matchScore;

    @Column(name = "culture_score")
    private Double cultureScore;

    @Column(name = "reason_summary", columnDefinition = "TEXT")
    private String reasonSummary;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}