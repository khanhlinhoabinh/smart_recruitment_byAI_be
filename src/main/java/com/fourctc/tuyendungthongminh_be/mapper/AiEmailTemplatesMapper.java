package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.AiEmailTemplatesDTO;
import com.fourctc.tuyendungthongminh_be.entity.AiEmailTemplates;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface AiEmailTemplatesMapper {
    AiEmailTemplatesMapper INSTANCE = Mappers.getMapper(AiEmailTemplatesMapper.class);

    AiEmailTemplatesDTO aiEmailTemplatesEntityToAiEmailTemplatesDTO(AiEmailTemplates aiEmailTemplates);

    AiEmailTemplates aiEmailTemplatesDTOToAiEmailTemplatesEntity(AiEmailTemplatesDTO aiEmailTemplatesDTO);
}
