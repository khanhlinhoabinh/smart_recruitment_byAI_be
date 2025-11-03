package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.AiMatchResultsDTO;
import com.fourctc.tuyendungthongminh_be.entity.AiMatchResults;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface AiMatchResultsMapper {
    AiMatchResultsMapper INSTANCE = Mappers.getMapper(AiMatchResultsMapper.class);

    AiMatchResultsDTO aiMatchResultsEntityToAiMatchResultsDTO(AiMatchResults aiMatchResults);

    AiMatchResults aiMatchResultsDTOToAiMatchResultsEntity(AiMatchResultsDTO aiMatchResultsDTO);
}
