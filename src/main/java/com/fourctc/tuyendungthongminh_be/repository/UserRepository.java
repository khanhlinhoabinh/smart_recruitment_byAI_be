package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Bạn có thể thêm các truy vấn tùy chỉnh ở đây nếu cần

    // Ví dụ: Tìm User theo email
    User findByEmail(String email);

    // Ví dụ: Tìm User theo status
    List<User> findByStatus(User.Status status);
    User findByResetToken(String resetToken);
}
