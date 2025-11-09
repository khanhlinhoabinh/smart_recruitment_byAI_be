package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.JobDTO;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface JobMapper {
    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    @Mapping(source = "company.companyId", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "approvedBy.userId", target = "approvedByUserId")
    @Mapping(source = "approvedBy.fullName", target = "approvedByFullName")
    @Mapping(source = "createdBy", target = "createdBy") // thêm mapping createdBy
    JobDTO toDTO(Job job);

    Job toEntity(JobDTO jobDTO);
}
