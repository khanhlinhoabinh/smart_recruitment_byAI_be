package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.TemplateDTO;
import com.fourctc.tuyendungthongminh_be.entity.Template;
import com.fourctc.tuyendungthongminh_be.mapper.TemplateMapper;
import com.fourctc.tuyendungthongminh_be.repository.TemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
    public TemplateDTO updateTemplate(UUID id, TemplateDTO dto) {
        Template existing = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template không tồn tại"));

        existing.setName(dto.getName());
        existing.setPreviewImage(dto.getPreviewImage());
        existing.setHtmlLayout(dto.getHtmlLayout());

        Template updated = templateRepository.save(existing);
        return templateMapper.entityToDTO(updated);
    }

    public void deleteTemplate(UUID id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template không tồn tại");
        }
        templateRepository.deleteById(id);
    }
}