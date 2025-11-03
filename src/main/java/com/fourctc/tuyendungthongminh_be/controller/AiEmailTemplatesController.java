package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.AiEmailTemplatesDTO;
import com.fourctc.tuyendungthongminh_be.service.AiEmailTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ai-email-templates")
public class AiEmailTemplatesController {

    @Autowired
    private AiEmailTemplatesService aiEmailTemplatesService;

}
