
package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Application;
import com.fourctc.tuyendungthongminh_be.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByCandidate(Candidate candidate);
    Page<Application> findByJob_JobId(UUID jobId, Pageable pageable);
    Page<Application> findByJob_JobIdAndStatus(UUID jobId, Application.ApplicationStatus status, Pageable pageable);
}
