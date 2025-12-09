
package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationDTO;
import com.fourctc.tuyendungthongminh_be.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "job.jobId", target = "jobId")
    @Mapping(source = "job.title", target = "jobTitle")
    @Mapping(source = "candidate.candidateId", target = "candidateId")
    @Mapping(source = "candidate.user.fullName", target = "candidateName")
    @Mapping(source = "candidate.user.email", target = "email")
    @Mapping(source = "cv.id", target = "cvId")
    @Mapping(source = "cv.title", target = "cvTitle")
    // enum -> String
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    // Timestamp -> LocalDateTime
    @Mapping(source = "appliedAt", target = "appliedAt", qualifiedByName = "tsToLdt")
    ApplicationDTO applicationEntityToApplicationDTO(Application application);

    // (tuỳ chọn) map List cho gọn
    List<ApplicationDTO> toDTOs(List<Application> applications);

    // ===== Converters =====

    @Named("statusToString")
    default String statusToString(Application.ApplicationStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("tsToLdt")
    default LocalDateTime tsToLdt(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
