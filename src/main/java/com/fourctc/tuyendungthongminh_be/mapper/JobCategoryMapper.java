package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.JobCategoryDTO;
import com.fourctc.tuyendungthongminh_be.entity.JobCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobCategoryMapper {
    JobCategoryDTO toDTO(JobCategory entity);
    JobCategory toEntity(JobCategoryDTO dto);
}