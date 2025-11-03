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
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "candidate_id", nullable = false, unique = true)
    private UUID candidateId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "headline", length = 150)
    private String headline;

    @ManyToOne
    @JoinColumn(name = "default_cv_id", referencedColumnName = "cv_id")
    private CV defaultCv;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public enum ProfileVisibility {
        PUBLIC, PRIVATE
    }
}