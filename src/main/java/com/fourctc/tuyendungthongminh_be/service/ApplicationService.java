
package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationStatusDTO;
import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.dto.ApplicationRequest;
import com.fourctc.tuyendungthongminh_be.entity.*;
import com.fourctc.tuyendungthongminh_be.mapper.ApplicationMapper;
import com.fourctc.tuyendungthongminh_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final CVRepository cvRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    /**
     * Tạo ứng tuyển mới cho ứng viên
     */
    public Application createApplication(ApplicationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null || user.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Người dùng không phải ứng viên");
        }

        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate không tồn tại"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));
        CV cv = cvRepository.findById(request.getCvId())
                .orElseThrow(() -> new RuntimeException("CV không tồn tại"));

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .cv(cv)
                .status(Application.ApplicationStatus.PENDING)
                .notes(request.getNotes())
                .appliedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return applicationRepository.save(application);
    }

    /**
     * Xem danh sách ứng tuyển của ứng viên
     */
    public List<ApplicationDTO> getApplicationsForCandidate(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null || user.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Người dùng không phải ứng viên");
        }

        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate không tồn tại"));

        List<Application> applications = applicationRepository.findByCandidate(candidate);
        return applications.stream()
                .map(applicationMapper::applicationEntityToApplicationDTO)
                .toList();
    }

    /**
     * Lấy danh sách ứng tuyển theo JobId (cho HR)
     */
    public Page<ApplicationDTO> getApplicationsByJobId(UUID jobId, Application.ApplicationStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = (status != null)
                ? applicationRepository.findByJob_JobIdAndStatus(jobId, status, pageable)
                : applicationRepository.findByJob_JobId(jobId, pageable);

        return applications.map(applicationMapper::applicationEntityToApplicationDTO);
    }

    /**
     * Xem chi tiết ứng tuyển
     */
    public ApplicationDTO getApplicationDetail(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application không tồn tại"));
        return applicationMapper.applicationEntityToApplicationDTO(application);
    }

    /**
     * Cập nhật trạng thái ứng tuyển
     */
    public ApplicationDTO updateApplicationStatus(UUID applicationId, Application.ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application không tồn tại"));

        application.setStatus(newStatus);
        applicationRepository.save(application);

        return applicationMapper.applicationEntityToApplicationDTO(application);
    }


// Kiểm tra trạng thái ứng tuyển của ứng viên (lấy từ email đăng nhập) cho một Job
    public ApplicationStatusDTO getMyApplicationStatus(String userEmail, UUID jobId) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null || user.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Người dùng không phải ứng viên");
        }

        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate không tồn tại"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));

        long count = applicationRepository.countByCandidateAndJob(candidate, job);

        // Repo trả Timestamp; convert sang LocalDateTime để JSON đẹp
        java.sql.Timestamp lastTs = applicationRepository.findLastAppliedAt(candidate, job);
        LocalDateTime lastAppliedAt = (lastTs != null) ? lastTs.toLocalDateTime() : null;

        return new ApplicationStatusDTO(count > 0, count, lastAppliedAt);
    }

}
