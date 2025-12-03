
package com.fourctc.tuyendungthongminh_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

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

    @Column(name = "tax_code", nullable = false, unique = true, length = 50)
    private String taxCode;

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
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "verify", nullable = false)
    private Verify verify = Verify.PENDING;

    @Column(name = "featured", nullable = false)
    private boolean featured = false;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public enum Status { ACTIVE, INACTIVE }

    public enum Verify { APPROVE, PENDING, REJECT }

    public enum CompanySize { SMALL, MEDIUM, LARGE, ENTERPRISE }

    @Column(name = "business_registration_url", length = 500)
    private String businessRegistrationUrl;

    @Column(name = "business_registration_file_name", length = 200)
    private String businessRegistrationFileName;

    @Column(name = "business_registration_uploaded_at")
    private java.sql.Timestamp businessRegistrationUploadedAt;
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "rejected_at")
    private Timestamp rejectedAt;
}
