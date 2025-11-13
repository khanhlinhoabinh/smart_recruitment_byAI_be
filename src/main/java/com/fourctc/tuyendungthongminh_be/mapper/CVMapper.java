package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CVMapper {

    @Mapping(source = "template.id", target = "templateId")
    CVDTO cvEntityToCVDTO(CV cv);

    @Mapping(target = "template.id", source = "templateId")
    CV cvDTOToCVEntity(CVDTO cvDTO);
}