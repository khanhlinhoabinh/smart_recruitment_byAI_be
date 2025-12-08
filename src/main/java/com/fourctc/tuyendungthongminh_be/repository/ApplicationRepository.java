
package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Application;
import com.fourctc.tuyendungthongminh_be.entity.Candidate;
import com.fourctc.tuyendungthongminh_be.entity.Job; // <<< THÊM IMPORT NÀY
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    // Danh sách theo ứng viên
    List<Application> findByCandidate(Candidate candidate);

    // Danh sách theo jobId (HR)
    Page<Application> findByJob_JobId(UUID jobId, Pageable pageable);
    Page<Application> findByJob_JobIdAndStatus(UUID jobId, Application.ApplicationStatus status, Pageable pageable);

    // Tìm theo cặp Candidate + Job (dùng entity)
    Optional<Application> findByCandidateAndJob(Candidate candidate, Job job);
    boolean existsByCandidateAndJob(Candidate candidate, Job job);

    // Tìm theo cặp bằng ID (không cần load entity trước)
    Optional<Application> findByCandidate_CandidateIdAndJob_JobId(UUID candidateId, UUID jobId);
    boolean existsByCandidate_CandidateIdAndJob_JobId(UUID candidateId, UUID jobId);
}
