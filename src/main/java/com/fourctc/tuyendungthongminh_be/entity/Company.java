package com.fourctc.tuyendungthongminh_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

// package com.fourctc.tuyendungthongminh_be.entity.Company

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "company_id", nullable = false, unique = true)
    private UUID companyId;

    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "cover_url", length = 255)
    private String coverUrl;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private CompanySize size = CompanySize.MEDIUM;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE; // Mới: chờ duyệt

    @Column(name = "featured", nullable = false)
    private boolean featured = false;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "created_by", length = 100)
    private String createdBy; // Người tạo (HR)

    public enum Status {
        ACTIVE, INACTIVE
    }

    public enum CompanySize {
        SMALL, MEDIUM, LARGE, ENTERPRISE
    }
}