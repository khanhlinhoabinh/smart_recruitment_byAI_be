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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.fourctc.tuyendungthongminh_be.entity.Employer;   // THÊM DÒNG NÀY
import com.fourctc.tuyendungthongminh_be.entity.Company;
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

    // Lấy danh sách 10 job mới nhất theo ngày đăng (createdAt)
    public List<JobDTO> getLatestJobs() {
        Pageable pageable = PageRequest.of(0, 9); // Lấy 10 job đầu tiên
        List<Job> jobs = jobRepository.findLatestJobs(Job.JobStatus.APPROVED, pageable);
        return jobs.stream().map(jobMapper::toDTO).collect(Collectors.toList());
    }

    // Tìm việc theo từ khóa, vị trí, ngành nghề
    public List<JobDTO> searchJobs(String keyword, String location, UUID categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Job> jobs = jobRepository.searchJobs(Job.JobStatus.APPROVED, keyword, location, categoryId, pageable);
        return jobs.stream().map(jobMapper::toDTO).collect(Collectors.toList());
    }

    // ✅ Lấy chi tiết job theo ID
    public JobDTO getJobById(UUID id) {
        return jobRepository.findById(id)
                .map(jobMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));
    }

    // === THÊM VÀO CUỐI JobService.java (trước dấu } cuối cùng) ===
    public List<JobDTO> getJobsOfMyCompany(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("Không xác thực được người dùng");
        }

        String email = principal.getName();

        // Tìm Employer theo email (dùng method đã có sẵn trong EmployerRepository)
        Employer employer = employerRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ nhà tuyển dụng của bạn"));

        // Kiểm tra đã xác thực chưa (field boolean verified trong entity)
        if (!employer.isVerified()) {
            throw new SecurityException("Hồ sơ nhà tuyển dụng chưa được xác thực");
        }

        // Lấy companyId từ employer → entity Employer có field Company company
        Company company = employer.getCompany();
        if (company == null || company.getCompanyId() == null) {
            throw new IllegalStateException("Nhà tuyển dụng chưa được liên kết với công ty nào");
        }

        UUID companyId = company.getCompanyId();

        // Lấy tất cả job của công ty đó
        return jobRepository.findByCompany_CompanyId(companyId).stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }

}
