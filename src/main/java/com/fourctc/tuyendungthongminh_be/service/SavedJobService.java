package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.SavedJobDTO;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import com.fourctc.tuyendungthongminh_be.entity.SavedJob;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.repository.JobRepository;
import com.fourctc.tuyendungthongminh_be.repository.SavedJobRepository;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public void saveJob(UUID userId, UUID jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (savedJobRepository.findByUserAndJob(user, job).isPresent()) {
            throw new RuntimeException("Job already saved");
        }

        SavedJob savedJob = SavedJob.builder()
                .user(user)
                .job(job)
                .createdAt(LocalDateTime.now())
                .build();

        savedJobRepository.save(savedJob);
    }

    public List<SavedJobDTO> getSavedJobs(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return savedJobRepository.findByUser(user).stream()
                .map(savedJob -> {
                    Job job = savedJob.getJob();
                    String salaryRange = "";
                    if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
                        salaryRange = job.getSalaryMin() + " - " + job.getSalaryMax() + " triệu";
                    }
                    return SavedJobDTO.builder()
                            .jobId(job.getJobId())
                            .jobTitle(job.getTitle())
                            .companyName(job.getCompany() != null ? job.getCompany().getName() : "")
                            .location(job.getLocation())
                            .salaryRange(salaryRange)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void deleteSavedJob(UUID userId, UUID jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        SavedJob savedJob = savedJobRepository.findByUserAndJob(user, job)
                .orElseThrow(() -> new RuntimeException("Saved job not found"));

        savedJobRepository.delete(savedJob);
    }
}
