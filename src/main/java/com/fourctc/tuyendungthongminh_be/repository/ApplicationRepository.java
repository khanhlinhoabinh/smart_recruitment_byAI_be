
package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Application;
import com.fourctc.tuyendungthongminh_be.entity.Candidate;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByCandidate(Candidate candidate);

    Page<Application> findByJob_JobId(UUID jobId, Pageable pageable);

    Page<Application> findByJob_JobIdAndStatus(UUID jobId, Application.ApplicationStatus status, Pageable pageable);

    // ✅ Đếm số lần ứng tuyển theo candidate + job
    long countByCandidateAndJob(Candidate candidate, Job job);

    // ✅ Lấy thời điểm ứng gần nhất (Timestamp vì entity dùng Timestamp)
    @Query("""
        select max(a.appliedAt)
        from Application a
        where a.candidate = :candidate and a.job = :job
    """)
    Timestamp findLastAppliedAt(@Param("candidate") Candidate candidate,
                                @Param("job") Job job);
}
