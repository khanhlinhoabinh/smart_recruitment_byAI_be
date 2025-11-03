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
@Table(name = "ai_email_templates")
public class AiEmailTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "template_id", nullable = false, unique = true)
    private UUID templateId;

    @Column(name = "template_name", nullable = false, length = 150)
    private String templateName;

    @Column(name = "subject", length = 150)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}