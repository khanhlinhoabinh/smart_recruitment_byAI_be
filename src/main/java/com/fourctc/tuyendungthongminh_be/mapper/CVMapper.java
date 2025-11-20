package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {JsonMapConverter.class})
public interface CVMapper {

    @Mapping(source = "template.id", target = "templateId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "data", target = "data", qualifiedByName = "stringToMap")
    CVDTO cvEntityToCVDTO(CV cv);

    @Mapping(target = "template.id", source = "templateId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(source = "data", target = "data", qualifiedByName = "mapToString")
    CV cvDTOToCVEntity(CVDTO cvDTO);
}