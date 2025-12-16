package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.JobCategoryDTO;
import com.fourctc.tuyendungthongminh_be.entity.JobCategory;
import com.fourctc.tuyendungthongminh_be.mapper.JobCategoryMapper;
import com.fourctc.tuyendungthongminh_be.repository.JobCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Timestamp;

@Service
public class JobCategoryService {

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Autowired
    private JobCategoryMapper jobCategoryMapper;

    public List<JobCategoryDTO> getPopularCategories() {
        return jobCategoryRepository.findAll().stream()
                .filter(JobCategory::isPopular)
                .map(jobCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<JobCategoryDTO> getAllCategories() {
        return jobCategoryRepository.findAll().stream()
                .map(jobCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public JobCategoryDTO createCategory(JobCategoryDTO dto, String createdBy) {
        JobCategory entity = new JobCategory();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPopular(dto.isPopular());
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setIconUrl(dto.getIconUrl()); // THÊM DÒNG NÀY

        JobCategory saved = jobCategoryRepository.save(entity);
        return jobCategoryMapper.toDTO(saved);
    }

    public JobCategoryDTO updateCategory(UUID id, JobCategoryDTO dto) {
        JobCategory existing = jobCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPopular(dto.isPopular());
        existing.setIconUrl(dto.getIconUrl()); // THÊM DÒNG NÀY

        JobCategory updated = jobCategoryRepository.save(existing);
        return jobCategoryMapper.toDTO(updated);
    }

    public void deleteCategory(UUID id) {
        jobCategoryRepository.deleteById(id);
    }
}