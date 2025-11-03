package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface CVMapper {
    CVMapper INSTANCE = Mappers.getMapper(CVMapper.class);

    CVDTO cvEntityToCVDTO(CV cv);

    CV cvDTOToCVEntity(CVDTO cvDTO);
}
