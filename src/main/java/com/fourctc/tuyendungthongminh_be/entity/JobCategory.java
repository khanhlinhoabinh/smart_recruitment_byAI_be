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
@Table(name = "job_categories")
public class JobCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id", nullable = false, unique = true)
    private UUID categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "category_id")
    private JobCategory parentCategory;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular = false;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}