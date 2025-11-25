package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByStatus(Job.JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.status = :status ORDER BY j.createdAt DESC")
    List<Job> findLatestJobs(@Param("status") Job.JobStatus status, Pageable pageable);

    List<Job> findByCompany_CompanyId(UUID companyId);
    @Query("SELECT j FROM Job j WHERE j.status = :status " +
            "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:categoryId IS NULL OR j.category.categoryId = :categoryId) " +
            "ORDER BY j.createdAt DESC")
    List<Job> searchJobs(@Param("status") Job.JobStatus status,
                         @Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("categoryId") UUID categoryId,
                         Pageable pageable);
}
