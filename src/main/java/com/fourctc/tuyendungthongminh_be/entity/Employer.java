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
@Table(name = "employers")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "employer_id", nullable = false, unique = true)
    private UUID employerId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    private Company company;

    @Column(name = "position_title", length = 100)
    private String positionTitle;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "work_email", length = 150, unique = true)
    private String workEmail;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    public enum Status {
        ACTIVE, INACTIVE, BANNED
    }
}
