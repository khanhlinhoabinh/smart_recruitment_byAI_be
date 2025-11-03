package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.SystemAlertsDTO;
import com.fourctc.tuyendungthongminh_be.entity.SystemAlerts;
import com.fourctc.tuyendungthongminh_be.mapper.SystemAlertsMapper;
import com.fourctc.tuyendungthongminh_be.repository.SystemAlertsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SystemAlertsService {

    @Autowired
    private SystemAlertsRepository systemAlertsRepository;

    @Autowired
    private SystemAlertsMapper systemAlertsMapper;

}
