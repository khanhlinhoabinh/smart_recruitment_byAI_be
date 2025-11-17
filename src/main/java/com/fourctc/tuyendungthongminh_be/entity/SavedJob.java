package com.fourctc.tuyendungthongminh_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saved_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
