package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

    public interface UserRepository extends JpaRepository<User, Long> {

        // Tìm user theo email (dùng cho đăng nhập)
        Optional<User> findByEmail(String email);

        // Kiểm tra email đã tồn tại (dùng cho đăng ký)
        boolean existsByEmail(String email);
    }
