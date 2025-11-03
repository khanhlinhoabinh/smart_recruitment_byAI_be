package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.AiCvParsingDTO;
import com.fourctc.tuyendungthongminh_be.entity.AiCvParsing;
import com.fourctc.tuyendungthongminh_be.mapper.AiCvParsingMapper;
import com.fourctc.tuyendungthongminh_be.repository.AiCvParsingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AiCvParsingService {

    @Autowired
    private AiCvParsingRepository aiCvParsingRepository;

    @Autowired
    private AiCvParsingMapper aiCvParsingMapper;


}
