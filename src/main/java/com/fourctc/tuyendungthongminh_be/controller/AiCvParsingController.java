package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.AiCvParsingDTO;
import com.fourctc.tuyendungthongminh_be.service.AiCvParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ai-cv-parsing")
public class AiCvParsingController {

    @Autowired
    private AiCvParsingService aiCvParsingService;

}
