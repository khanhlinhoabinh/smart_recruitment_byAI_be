// src/main/java/com/fourctc/tuyendungthongminh_be/repository/EmployerRepository.java
package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Employer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, UUID> {

    @EntityGraph(attributePaths = {"user", "company"})
    Optional<Employer> findByEmployerId(UUID employerId);


    @EntityGraph(attributePaths = {"user", "company"})
    List<Employer> findAll(); // ✅ thêm để list luôn có user/company

    @EntityGraph(attributePaths = {"user", "company"})
    Optional<Employer> findByUser_Email(String email);

    boolean existsByWorkEmail(String workEmail);

    boolean existsByWorkEmailAndEmployerIdNot(String workEmail, UUID employerId);


// Danh sách employer chưa được verify, KHÔNG phụ thuộc vào việc đã upload GPKD
    @EntityGraph(attributePaths = {"user", "company"})
    List<Employer> findByVerifiedFalse();

    List<Employer> findByCompany_CompanyId(UUID companyId);
    List<Employer> findByCompany_CompanyIdAndVerifiedTrue(UUID companyId);
}
