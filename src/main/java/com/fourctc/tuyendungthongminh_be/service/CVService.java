package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import com.fourctc.tuyendungthongminh_be.entity.Template;
import com.fourctc.tuyendungthongminh_be.mapper.CVMapper;
import com.fourctc.tuyendungthongminh_be.repository.CVRepository;
import com.fourctc.tuyendungthongminh_be.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ObjectMapper objectMapper;

    // ==============================
    // CREATE CV WITH AUTH
    // ==============================
    public CVDTO createCVWithAuth(CVDTO dto, UUID currentUserId) {
        CV cv = cvMapper.cvDTOToCVEntity(dto);
        cv.setUserId(currentUserId);

        cv.setVisibility(dto.getVisibility() == null || dto.getVisibility().isEmpty()
                ? CV.Visibility.PRIVATE
                : CV.Visibility.valueOf(dto.getVisibility().toUpperCase()));

        cv.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        cv.setUpdatedAt(cv.getCreatedAt());

        // Convert Map -> JSON
        if (dto.getData() != null && !dto.getData().isEmpty()) {
            try {
                cv.setData(objectMapper.writeValueAsString(dto.getData()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Lỗi lưu dữ liệu CV");
            }
        }

        // ⭐ FIX LỖI TRANSIENT TEMPLATE
        if (dto.getTemplateId() != null) {
            Template template = templateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));
            cv.setTemplate(template);    // Gán Template đã tồn tại
        } else {
            cv.setTemplate(null);        // Upload file PDF -> không dùng template
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

    // ==============================
    // RENDER CV (HTML + DATA)
    // ==============================
    public Map<String, Object> renderCV(UUID cvId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("htmlLayout", cv.getTemplate() != null ? cv.getTemplate().getHtmlLayout() : "");

        if (cv.getData() != null) {
            try {
                Map<String, Object> data = objectMapper.readValue(cv.getData(), new TypeReference<>() {});
                result.put("data", data);
            } catch (Exception e) {
                result.put("data", new HashMap<>());
            }
        } else {
            result.put("data", new HashMap<>());
        }

        return result;
    }
    public CVDTO updateCV(UUID cvId, CVDTO dto, UUID currentUserId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found"));

        if (!cv.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Không có quyền sửa CV này");
        }

        cv.setTitle(dto.getTitle());
        cv.setUpdatedAt(new Timestamp(System.currentTimeMillis()));


// ✅ Cập nhật visibility nếu FE gửi lên
          if (dto.getVisibility() != null && !dto.getVisibility().isBlank()) {
              cv.setVisibility(CV.Visibility.valueOf(dto.getVisibility().toUpperCase()));
              }
                   // ✅ QUAN TRỌNG: cập nhật Template theo templateId mới
                            if (dto.getTemplateId() != null) {
                  Template template = templateRepository.findById(dto.getTemplateId())
                               .orElseThrow(() -> new RuntimeException("Template not found"));
                    cv.setTemplate(template);
               }
           // (Nếu muốn cho phép xóa template, thêm else { cv.setTemplate(null); })


        if (dto.getData() != null) {
            try {
                cv.setData(objectMapper.writeValueAsString(dto.getData()));
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi cập nhật dữ liệu CV");
            }
        }

        CV saved = cvRepository.save(cv);
        return cvMapper.cvEntityToCVDTO(saved);
    }
}