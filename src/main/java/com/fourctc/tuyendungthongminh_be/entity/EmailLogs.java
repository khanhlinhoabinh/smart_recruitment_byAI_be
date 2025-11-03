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
@Table(name = "email_logs")
public class EmailLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "log_id", nullable = false, unique = true)
    private UUID logId;

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "user_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "template_id", referencedColumnName = "template_id")
    private AiEmailTemplates emailTemplate;

    @Column(name = "subject", length = 150)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "sent_at")
    private Timestamp sentAt;
}