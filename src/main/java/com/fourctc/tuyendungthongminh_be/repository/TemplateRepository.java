package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {

}