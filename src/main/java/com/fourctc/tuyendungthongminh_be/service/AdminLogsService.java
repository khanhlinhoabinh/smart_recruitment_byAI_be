package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.AdminLogsDTO;
import com.fourctc.tuyendungthongminh_be.entity.AdminLogs;
import com.fourctc.tuyendungthongminh_be.mapper.AdminLogsMapper;
import com.fourctc.tuyendungthongminh_be.repository.AdminLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminLogsService {

    @Autowired
    private AdminLogsRepository adminLogsRepository;

    @Autowired
    private AdminLogsMapper adminLogsMapper;

}
