package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationHistoryDTO;
import com.fourctc.tuyendungthongminh_be.entity.ApplicationHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface ApplicationHistoryMapper {
    ApplicationHistoryMapper INSTANCE = Mappers.getMapper(ApplicationHistoryMapper.class);

    ApplicationHistoryDTO applicationHistoryEntityToApplicationHistoryDTO(ApplicationHistory applicationHistory);

    ApplicationHistory applicationHistoryDTOToApplicationHistoryEntity(ApplicationHistoryDTO applicationHistoryDTO);
}
