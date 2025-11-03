package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.AiMatchResultsDTO;
import com.fourctc.tuyendungthongminh_be.service.AiMatchResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ai-match-results")
public class AiMatchResultsController {

    @Autowired
    private AiMatchResultsService aiMatchResultsService;


}
