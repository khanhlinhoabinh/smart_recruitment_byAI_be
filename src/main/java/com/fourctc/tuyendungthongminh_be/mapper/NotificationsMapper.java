package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.NotificationsDTO;
import com.fourctc.tuyendungthongminh_be.entity.Notifications;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface NotificationsMapper {
    NotificationsMapper INSTANCE = Mappers.getMapper(NotificationsMapper.class);

    NotificationsDTO notificationsEntityToNotificationsDTO(Notifications notifications);

    Notifications notificationsDTOToNotificationsEntity(NotificationsDTO notificationsDTO);
}
