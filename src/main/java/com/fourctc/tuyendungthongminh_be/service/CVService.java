package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.entity.CV;
import com.fourctc.tuyendungthongminh_be.mapper.CVMapper;
import com.fourctc.tuyendungthongminh_be.repository.CVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private CVMapper cvMapper;

}
