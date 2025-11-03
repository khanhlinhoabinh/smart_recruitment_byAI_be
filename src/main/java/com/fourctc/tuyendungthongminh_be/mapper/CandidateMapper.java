package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.CandidateDTO;
import com.fourctc.tuyendungthongminh_be.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface CandidateMapper {
    CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

    CandidateDTO candidateEntityToCandidateDTO(Candidate candidate);

    Candidate candidateDTOToCandidateEntity(CandidateDTO candidateDTO);
}
