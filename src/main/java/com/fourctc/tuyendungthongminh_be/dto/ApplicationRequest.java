
package com.fourctc.tuyendungthongminh_be.dto;

import lombok.Data;
import java.util.UUID;

/**
 * DTO cho request tạo mới Application.
 * Chỉ chứa các thông tin cần thiết từ client.
 */
@Data
public class ApplicationRequest {
    private UUID jobId;        // ID của Job mà ứng viên ứng tuyển
    private UUID candidateId;  // ID của Candidate
    private UUID cvId;         // ID của CV đã upload
    private String notes;      // Ghi chú thêm (nếu có)
}
