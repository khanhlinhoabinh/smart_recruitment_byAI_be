package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Tìm role theo tên (ví dụ: USER, HR, ADMIN)
    Optional<Role> findByName(String name);

    // Kiểm tra role đã tồn tại
    boolean existsByName(String name);
}
