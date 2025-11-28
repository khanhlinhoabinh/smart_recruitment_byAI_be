
package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "job.jobId", target = "jobId")
    @Mapping(source = "job.title", target = "jobTitle") // Lấy title từ Job
    @Mapping(source = "candidate.candidateId", target = "candidateId")
    @Mapping(source = "candidate.user.fullName", target = "candidateName") // Lấy tên từ User trong Candidate
    @Mapping(source = "candidate.user.email", target = "email")
    @Mapping(source = "cv.id", target = "cvId")
    @Mapping(source = "cv.title", target = "cvTitle") // Lấy title từ CV
    ApplicationDTO applicationEntityToApplicationDTO(Application application);
}
