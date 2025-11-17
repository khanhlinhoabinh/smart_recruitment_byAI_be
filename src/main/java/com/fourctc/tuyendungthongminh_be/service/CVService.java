package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import com.fourctc.tuyendungthongminh_be.entity.Template;
import com.fourctc.tuyendungthongminh_be.repository.CVRepository;
import com.fourctc.tuyendungthongminh_be.repository.TemplateRepository;
import com.fourctc.tuyendungthongminh_be.mapper.CVMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private CVMapper cvMapper;

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
}