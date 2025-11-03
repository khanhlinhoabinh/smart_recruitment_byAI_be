package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import com.fourctc.tuyendungthongminh_be.entity.Employer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface EmployerMapper {
    EmployerMapper INSTANCE = Mappers.getMapper(EmployerMapper.class);

    EmployerDTO employerEntityToEmployerDTO(Employer employer);

    Employer employerDTOToEmployerEntity(EmployerDTO employerDTO);
}
