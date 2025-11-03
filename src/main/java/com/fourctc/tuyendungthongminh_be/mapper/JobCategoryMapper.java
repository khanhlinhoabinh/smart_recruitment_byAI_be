package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.JobCategoryDTO;
import com.fourctc.tuyendungthongminh_be.entity.JobCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface JobCategoryMapper {
    JobCategoryMapper INSTANCE = Mappers.getMapper(JobCategoryMapper.class);

    JobCategoryDTO jobCategoryEntityToJobCategoryDTO(JobCategory jobCategory);

    JobCategory jobCategoryDTOToJobCategoryEntity(JobCategoryDTO jobCategoryDTO);
}
