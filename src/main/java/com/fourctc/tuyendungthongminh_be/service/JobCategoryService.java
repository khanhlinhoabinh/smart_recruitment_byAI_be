package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.JobCategoryDTO;
import com.fourctc.tuyendungthongminh_be.entity.JobCategory;
import com.fourctc.tuyendungthongminh_be.mapper.JobCategoryMapper;
import com.fourctc.tuyendungthongminh_be.repository.JobCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JobCategoryService {

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Autowired
    private JobCategoryMapper jobCategoryMapper;


}
