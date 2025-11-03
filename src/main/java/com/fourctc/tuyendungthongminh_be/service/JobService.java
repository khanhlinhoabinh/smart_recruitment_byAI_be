package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.JobDTO;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import com.fourctc.tuyendungthongminh_be.mapper.JobMapper;
import com.fourctc.tuyendungthongminh_be.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

}
