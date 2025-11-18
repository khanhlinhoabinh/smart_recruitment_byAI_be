package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CVRepository extends JpaRepository<CV, UUID> {
    List<CV> findByUserId(UUID userId);
}