package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import com.fourctc.tuyendungthongminh_be.entity.Template;
import com.fourctc.tuyendungthongminh_be.mapper.CVMapper;
import com.fourctc.tuyendungthongminh_be.repository.CVRepository;
import com.fourctc.tuyendungthongminh_be.repository.TemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private CVMapper cvMapper;

    // ==============================
    // CREATE CV
    // ==============================
    public CVDTO createCV(CVDTO dto) {
        CV cv = cvMapper.cvDTOToCVEntity(dto);

        if (dto.getTemplateId() != null) {
            Template template = templateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));
            cv.setTemplate(template);
        }

        CV saved = cvRepository.save(cv);
        return cvMapper.cvEntityToCVDTO(saved);
    }

    // ==============================
    // GET ALL CV BY USER ID
    // ==============================
    public List<CVDTO> getCVsByUser(UUID userId) {
        return cvRepository.findByUserId(userId)
                .stream()
                .map(cvMapper::cvEntityToCVDTO)
                .collect(Collectors.toList());
    }

    // ==============================
    // DELETE CV BY CV ID
    // ==============================
    public void deleteCV(UUID cvId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found"));

        cvRepository.delete(cv);
    }
}