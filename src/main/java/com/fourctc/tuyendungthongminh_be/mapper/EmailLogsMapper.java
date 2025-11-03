package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.EmailLogsDTO;
import com.fourctc.tuyendungthongminh_be.entity.EmailLogs;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface EmailLogsMapper {
    EmailLogsMapper INSTANCE = Mappers.getMapper(EmailLogsMapper.class);

    EmailLogsDTO emailLogsEntityToEmailLogsDTO(EmailLogs emailLogs);

    EmailLogs emailLogsDTOToEmailLogsEntity(EmailLogsDTO emailLogsDTO);
}
