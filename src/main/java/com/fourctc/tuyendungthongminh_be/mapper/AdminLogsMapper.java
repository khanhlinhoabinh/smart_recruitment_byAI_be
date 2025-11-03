package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.AdminLogsDTO;
import com.fourctc.tuyendungthongminh_be.entity.AdminLogs;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface AdminLogsMapper {
    AdminLogsMapper INSTANCE = Mappers.getMapper(AdminLogsMapper.class);

    AdminLogsDTO adminLogsEntityToAdminLogsDTO(AdminLogs adminLogs);

    AdminLogs adminLogsDTOToAdminLogsEntity(AdminLogsDTO adminLogsDTO);
}
