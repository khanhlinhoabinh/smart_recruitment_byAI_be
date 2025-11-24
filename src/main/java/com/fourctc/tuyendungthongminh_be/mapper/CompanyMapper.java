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

// GPKD: thường không set từ DTO khi create; backend sẽ set ở upload
    @Mapping(target = "businessRegistrationUrl", ignore = true)
    @Mapping(target = "businessRegistrationFileName", ignore = true)
    @Mapping(target = "businessRegistrationUploadedAt", ignore = true)

    Company companyDTOToCompanyEntityForCreate(CompanyDTO dto);

    // Dùng cho update
    @Mapping(source = "size", target = "size")
    @Mapping(source = "status", target = "status")

    @Mapping(target = "businessRegistrationUrl", ignore = true)
    @Mapping(target = "businessRegistrationFileName", ignore = true)
    @Mapping(target = "businessRegistrationUploadedAt", ignore = true)

    Company companyDTOToCompanyEntityForUpdate(CompanyDTO dto);


    // ENTITY -> DTO: trả đầy đủ trường (bao gồm GPKD)
    @Mapping(source = "businessRegistrationUrl", target = "businessRegistrationUrl")
    @Mapping(source = "businessRegistrationFileName", target = "businessRegistrationFileName")
    @Mapping(source = "businessRegistrationUploadedAt", target = "businessRegistrationUploadedAt")


    CompanyDTO companyEntityToCompanyDTO(Company company);
}