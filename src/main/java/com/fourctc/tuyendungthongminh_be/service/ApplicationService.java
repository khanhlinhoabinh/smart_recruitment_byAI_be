
package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.dto.ApplicationRequest;
import com.fourctc.tuyendungthongminh_be.entity.*;
import com.fourctc.tuyendungthongminh_be.mapper.ApplicationMapper;
import com.fourctc.tuyendungthongminh_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- quan trọng

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
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
     * Upsert: nếu ứng viên đã ứng tuyển job này -> cập nhật đơn;
     * nếu chưa -> tạo mới.
     */
    @Transactional
    public Application upsertApplication(ApplicationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null || user.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Người dùng không phải ứng viên");
        }

        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate không tồn tại"));

        // Tối ưu: tìm đơn theo cặp (candidateId, jobId) trước
        Optional<Application> existingOpt =
                applicationRepository.findByCandidate_CandidateIdAndJob_JobId(
                        candidate.getCandidateId(), request.getJobId()
                );

        CV cv = cvRepository.findById(request.getCvId())
                .orElseThrow(() -> new RuntimeException("CV không tồn tại"));

        // (Khuyến nghị) kiểm tra CV có thuộc về candidate không
        // if (!cv.getCandidate().equals(candidate)) {
        //     throw new RuntimeException("CV không thuộc sở hữu của ứng viên này");
        // }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (existingOpt.isPresent()) {
            // --- CẬP NHẬT ĐƠN HIỆN CÓ ---
            Application application = existingOpt.get();
            application.setCv(cv);
            application.setNotes(request.getNotes());
            application.setAppliedAt(now);

            // Nghiệp vụ trạng thái: giữ nguyên hay đưa về PENDING tuỳ bạn
            // application.setStatus(Application.ApplicationStatus.PENDING);

            return applicationRepository.save(application);
        }

        // --- TẠO MỚI ---
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));

        // (Tuỳ chọn) chặn nếu job đã hết hạn
        // if (job.getExpiredAt() != null && job.getExpiredAt().before(new java.util.Date())) {
        //     throw new RuntimeException("Tin tuyển dụng đã hết hạn");
        // }

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .cv(cv)
                .status(Application.ApplicationStatus.PENDING)
                .notes(request.getNotes())
                .appliedAt(now)
                .build();

        try {
            return applicationRepository.save(application);
        } catch (DataIntegrityViolationException e) {
            // Phòng race condition: nếu unique (candidate_id, job_id) đã tồn tại do request song song
            // thì fallback sang update
            Optional<Application> reloaded =
                    applicationRepository.findByCandidate_CandidateIdAndJob_JobId(
                            candidate.getCandidateId(), request.getJobId()
                    );
            if (reloaded.isPresent()) {
                Application a = reloaded.get();
                a.setCv(cv);
                a.setNotes(request.getNotes());
                a.setAppliedAt(now);
                return applicationRepository.save(a);
            }
            // nếu vẫn lỗi, ném lại
            throw e;
        }
    }

    /**
     * (Giữ nguyên nếu nơi khác cần tạo mới thuần)
     * Tạo ứng tuyển mới cho ứng viên - KHÔNG khuyến nghị dùng nếu đã bật upsert.
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
}
