package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.EmailLogsDTO;
import com.fourctc.tuyendungthongminh_be.service.EmailLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/email-logs")
public class EmailLogsController {

    @Autowired
    private EmailLogsService emailLogsService;

}
