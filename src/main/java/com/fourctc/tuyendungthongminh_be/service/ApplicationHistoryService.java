package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationHistoryDTO;
import com.fourctc.tuyendungthongminh_be.entity.ApplicationHistory;
import com.fourctc.tuyendungthongminh_be.mapper.ApplicationHistoryMapper;
import com.fourctc.tuyendungthongminh_be.repository.ApplicationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApplicationHistoryService {

    @Autowired
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Autowired
    private ApplicationHistoryMapper applicationHistoryMapper;

}
