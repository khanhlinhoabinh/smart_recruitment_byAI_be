package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import com.fourctc.tuyendungthongminh_be.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "status", ignore = true) // BỎ QUA KHI TẠO
    @Mapping(target = "featured", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Company companyDTOToCompanyEntityForCreate(CompanyDTO dto);

    // Dùng cho update
    @Mapping(source = "size", target = "size")
    @Mapping(source = "status", target = "status")
    Company companyDTOToCompanyEntityForUpdate(CompanyDTO dto);

    CompanyDTO companyEntityToCompanyDTO(Company company);
}