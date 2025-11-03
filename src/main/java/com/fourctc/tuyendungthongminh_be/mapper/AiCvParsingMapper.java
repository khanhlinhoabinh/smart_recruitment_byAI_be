package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.AiCvParsingDTO;
import com.fourctc.tuyendungthongminh_be.entity.AiCvParsing;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface AiCvParsingMapper {
    AiCvParsingMapper INSTANCE = Mappers.getMapper(AiCvParsingMapper.class);

    AiCvParsingDTO aiCvParsingEntityToAiCvParsingDTO(AiCvParsing aiCvParsing);

    AiCvParsing aiCvParsingDTOToAiCvParsingEntity(AiCvParsingDTO aiCvParsingDTO);
}
