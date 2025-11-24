
package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationRequest;
import com.fourctc.tuyendungthongminh_be.entity.Application;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import com.fourctc.tuyendungthongminh_be.entity.Candidate;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final CVRepository cvRepository;
    private final UserRepository userRepository;

    public Application createApplication(ApplicationRequest request, String userEmail) {
        // Lấy User từ email
        User user = userRepository.findByEmail(userEmail);
        if (user == null || user.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Người dùng không phải ứng viên");
        }

        // Lấy Candidate từ User
        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate không tồn tại"));

        // Lấy Job và CV
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));
        CV cv = cvRepository.findById(request.getCvId())
                .orElseThrow(() -> new RuntimeException("CV không tồn tại"));

        // Tạo Application
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

    //Xem danh sách ứng tuyển của ứng viên
    public List<Application> getApplicationsForCandidate(String userEmail) {
        // Lấy User từ email
        User user = userRepository.findByEmail(userEmail);
        if (user == null || user.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Người dùng không phải ứng viên");
        }

        // Lấy Candidate từ User
        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Candidate không tồn tại"));

        // Lấy danh sách ứng tuyển của Candidate
        return applicationRepository.findByCandidate(candidate);
    }

}
