package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.JobDTO;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import com.fourctc.tuyendungthongminh_be.mapper.JobMapper;
import com.fourctc.tuyendungthongminh_be.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fourctc.tuyendungthongminh_be.entity.Job.JobStatus;
import com.fourctc.tuyendungthongminh_be.repository.CompanyRepository;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import com.fourctc.tuyendungthongminh_be.repository.JobCategoryRepository;
import com.fourctc.tuyendungthongminh_be.repository.EmployerRepository;
import com.fourctc.tuyendungthongminh_be.entity.User;


import java.util.UUID;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private UserRepository userRepository;

    // Xem tất cả job (HR, Admin)
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Xem các job đã được duyệt (public cho Candidate)
    public List<JobDTO> getApprovedJobs() {
        return jobRepository.findByStatus(JobStatus.APPROVED).stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }

    // HR thêm job → ở trạng thái "PENDING"
    public JobDTO createJob(JobDTO dto, Principal principal) {
        Job job = jobMapper.toEntity(dto);

        // Gán quan hệ Company & Category
        if (dto.getCompanyId() != null) {
            job.setCompany(companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found")));
        }
        if (dto.getCategoryId() != null) {
            job.setCategory(jobCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));
        }

        job.setStatus(JobStatus.PENDING);
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        job.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // Set email người tạo
        if (principal != null) {
            job.setCreatedBy(principal.getName());
        }
        if (job.getViewsCount() == null) {
            job.setViewsCount(0);
        }

        Job saved = jobRepository.save(job);
        return jobMapper.toDTO(saved);
    }


    // HR sửa job → quay lại trạng thái "PENDING"
    public JobDTO updateJob(UUID id, JobDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setExperienceRequired(dto.getExperienceRequired());
        job.setJobType(dto.getJobType() != null ? Job.JobType.valueOf(dto.getJobType()) : job.getJobType());
        job.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        job.setStatus(JobStatus.PENDING); // ✅ HR sửa => gửi yêu cầu duyệt lại
        if (job.getViewsCount() == null) {
            job.setViewsCount(0);
        }

        Job updated = jobRepository.save(job);
        return jobMapper.toDTO(updated);
    }

    // HR xóa job (không cần duyệt)
    public void deleteJob(UUID id) {
        if (!jobRepository.existsById(id)) {
            throw new IllegalArgumentException("Job not found");
        }
        jobRepository.deleteById(id);
    }
    // Admin duyệt job
    public JobDTO approveJob(UUID id, Principal principal) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Lấy email admin từ token
        String adminEmail = principal.getName();

        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }

        job.setStatus(Job.JobStatus.APPROVED);
        job.setApprovedBy(admin);
        job.setApprovedAt(new Timestamp(System.currentTimeMillis()));

        if (job.getViewsCount() == null) {
            job.setViewsCount(0);
        }

        Job saved = jobRepository.save(job);
        return jobMapper.toDTO(saved);
    }


}
