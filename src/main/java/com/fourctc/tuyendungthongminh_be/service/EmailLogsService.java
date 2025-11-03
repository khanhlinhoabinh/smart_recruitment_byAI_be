package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.EmailLogsDTO;
import com.fourctc.tuyendungthongminh_be.entity.EmailLogs;
import com.fourctc.tuyendungthongminh_be.mapper.EmailLogsMapper;
import com.fourctc.tuyendungthongminh_be.repository.EmailLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailLogsService {

    @Autowired
    private EmailLogsRepository emailLogsRepository;

    @Autowired
    private EmailLogsMapper emailLogsMapper;

}
