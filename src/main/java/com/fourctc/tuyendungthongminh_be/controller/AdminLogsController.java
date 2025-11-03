package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.AdminLogsDTO;
import com.fourctc.tuyendungthongminh_be.service.AdminLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin-logs")
public class AdminLogsController {

    @Autowired
    private AdminLogsService adminLogsService;

}
