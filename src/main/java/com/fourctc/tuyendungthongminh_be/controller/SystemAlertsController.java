package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.SystemAlertsDTO;
import com.fourctc.tuyendungthongminh_be.service.SystemAlertsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/system-alerts")
public class SystemAlertsController {

    @Autowired
    private SystemAlertsService systemAlertsService;

}
