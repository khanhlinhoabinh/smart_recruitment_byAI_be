
package com.fourctc.tuyendungthongminh_be.dto;

import java.time.LocalDateTime;

public record ApplicationStatusDTO(
        boolean hasApplied,
        long applicationCount,
        LocalDateTime lastAppliedAt
) {}
