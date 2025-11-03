package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.NotificationsDTO;
import com.fourctc.tuyendungthongminh_be.entity.Notifications;
import com.fourctc.tuyendungthongminh_be.mapper.NotificationsMapper;
import com.fourctc.tuyendungthongminh_be.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationsService {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private NotificationsMapper notificationsMapper;

}
