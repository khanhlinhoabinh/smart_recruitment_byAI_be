package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.AiEmailTemplatesDTO;
import com.fourctc.tuyendungthongminh_be.entity.AiEmailTemplates;
import com.fourctc.tuyendungthongminh_be.mapper.AiEmailTemplatesMapper;
import com.fourctc.tuyendungthongminh_be.repository.AiEmailTemplatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AiEmailTemplatesService {

    @Autowired
    private AiEmailTemplatesRepository aiEmailTemplatesRepository;

    @Autowired
    private AiEmailTemplatesMapper aiEmailTemplatesMapper;


}
