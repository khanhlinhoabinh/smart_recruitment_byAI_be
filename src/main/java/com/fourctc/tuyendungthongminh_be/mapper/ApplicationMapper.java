package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface ApplicationMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    ApplicationDTO applicationEntityToApplicationDTO(Application application);

    Application applicationDTOToApplicationEntity(ApplicationDTO applicationDTO);
}
