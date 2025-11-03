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
@Table(name = "application_history")
public class ApplicationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "history_id", nullable = false, unique = true)
    private UUID historyId;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "application_id")
    private Application application;

    @Column(name = "old_status", length = 50)
    private String oldStatus;

    @Column(name = "new_status", length = 50)
    private String newStatus;

    @ManyToOne
    @JoinColumn(name = "changed_by", referencedColumnName = "user_id")
    private User changedBy;

    @Column(name = "changed_at", nullable = false)
    private Timestamp changedAt = new Timestamp(System.currentTimeMillis());
}
