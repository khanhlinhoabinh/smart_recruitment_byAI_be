package com.fourctc.tuyendungthongminh_be.controller;

import com.fourctc.tuyendungthongminh_be.dto.CVDTO;
import com.fourctc.tuyendungthongminh_be.service.CVService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cv")
public class CVController {

    @Autowired
    private CVService cvService;

    @PostMapping("/create")
    public CVDTO createCV(@RequestBody CVDTO dto) {
        return cvService.createCV(dto);
    }
}