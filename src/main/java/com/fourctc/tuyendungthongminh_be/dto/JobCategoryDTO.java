package com.fourctc.tuyendungthongminh_be.dto;

import lombok.*;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryDTO {
    private UUID categoryId;
    private String name;
    private String description;
    private boolean popular;
    private String createdBy;
    private String iconUrl;                    // trả về cho frontend

    // Trường này chỉ dùng khi upload (không lưu vào DB)
}
