package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.TemplateDTO;
import com.fourctc.tuyendungthongminh_be.entity.Template;
import com.fourctc.tuyendungthongminh_be.mapper.TemplateMapper;
import com.fourctc.tuyendungthongminh_be.repository.TemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateMapper templateMapper;

    public TemplateDTO createTemplate(TemplateDTO dto) {
        Template t = templateMapper.dtoToEntity(dto);
        Template saved = templateRepository.save(t);
        return templateMapper.entityToDTO(saved);
    }

    public List<TemplateDTO> getAll() {
        return templateRepository.findAll()
                .stream().map(templateMapper::entityToDTO).toList();
    }
}