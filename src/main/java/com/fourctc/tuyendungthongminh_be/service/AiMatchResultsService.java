package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.AiMatchResultsDTO;
import com.fourctc.tuyendungthongminh_be.entity.AiMatchResults;
import com.fourctc.tuyendungthongminh_be.mapper.AiMatchResultsMapper;
import com.fourctc.tuyendungthongminh_be.repository.AiMatchResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AiMatchResultsService {

    @Autowired
    private AiMatchResultsRepository aiMatchResultsRepository;

    @Autowired
    private AiMatchResultsMapper aiMatchResultsMapper;


}
