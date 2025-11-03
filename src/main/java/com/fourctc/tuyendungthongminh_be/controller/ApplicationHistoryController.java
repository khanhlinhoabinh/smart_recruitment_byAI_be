package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.ApplicationHistoryDTO;
import com.fourctc.tuyendungthongminh_be.service.ApplicationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/application-history")
public class ApplicationHistoryController {

    @Autowired
    private ApplicationHistoryService applicationHistoryService;

}
