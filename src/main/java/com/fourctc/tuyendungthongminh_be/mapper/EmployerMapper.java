package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.EmployerDTO;
import com.fourctc.tuyendungthongminh_be.entity.Employer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.*;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface EmployerMapper {

    @Mappings({
            @Mapping(source = "user.userId", target = "userId"),
            @Mapping(source = "user.fullName", target = "fullName"),
            @Mapping(source = "user.email", target = "email"),
            @Mapping(source = "company.companyId", target = "companyId"),
            @Mapping(source = "company.name", target = "companyName")
            // Nếu bạn thêm companyBusinessRegistrationUrl vào DTO, map thêm:
            // @Mapping(source = "company.businessRegistrationUrl", target = "companyBusinessRegistrationUrl")
    })

    EmployerDTO employerEntityToEmployerDTO(Employer employer);

    @InheritInverseConfiguration
    Employer employerDTOToEmployerEntity(EmployerDTO employerDTO);
}
