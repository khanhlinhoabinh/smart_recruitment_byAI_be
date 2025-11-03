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
@Table(name = "admin_logs")
public class AdminLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "log_id", nullable = false, unique = true)
    private UUID logId;

    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private User admin;

    @Column(name = "action_type", length = 100)
    private String actionType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}