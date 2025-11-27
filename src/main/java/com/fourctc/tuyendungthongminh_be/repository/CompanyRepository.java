package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Company;
import com.fourctc.tuyendungthongminh_be.entity.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByStatusAndVerify(Company.Status status, Company.Verify verify);
    List<Company> findByStatus(Company.Status status);
    List<Company> findByNameContainingIgnoreCaseAndStatusAndVerify(String name, Company.Status status, Company.Verify verify);
    List<Company> findByFeaturedTrueAndStatus(Company.Status status);
    List<Company> findByNameContainingIgnoreCaseAndStatus(String name, Company.Status status);
    List<Company> findByFeaturedTrueAndStatusAndVerify(Company.Status status, Company.Verify verify);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByTaxCodeIgnoreCase(String taxCode);
}
