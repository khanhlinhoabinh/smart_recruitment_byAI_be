package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.SystemAlertsDTO;
import com.fourctc.tuyendungthongminh_be.entity.SystemAlerts;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface SystemAlertsMapper {
    SystemAlertsMapper INSTANCE = Mappers.getMapper(SystemAlertsMapper.class);

    SystemAlertsDTO systemAlertsEntityToSystemAlertsDTO(SystemAlerts systemAlerts);

    SystemAlerts systemAlertsDTOToSystemAlertsEntity(SystemAlertsDTO systemAlertsDTO);
}
