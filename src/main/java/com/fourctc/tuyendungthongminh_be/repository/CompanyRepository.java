package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Company;
import com.fourctc.tuyendungthongminh_be.entity.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByStatus(Company.Status status);
    List<Company> findByFeaturedTrueAndStatus(Company.Status status);

}