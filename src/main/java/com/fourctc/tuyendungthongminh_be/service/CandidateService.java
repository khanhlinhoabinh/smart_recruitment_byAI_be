package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CandidateDTO;
import com.fourctc.tuyendungthongminh_be.entity.Candidate;
import com.fourctc.tuyendungthongminh_be.mapper.CandidateMapper;
import com.fourctc.tuyendungthongminh_be.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateMapper candidateMapper;

}
