package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.mapper.UserMapper;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.sql.Timestamp;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;  // Giả sử bạn có UserRepository cho CRUD

    @Autowired
    private UserMapper userMapper;  // Tiêm vào UserMapper

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Hàm đăng ký người dùng
    public UserDTO registerUser(UserDTO userDTO) {
        // Kiểm tra email trùng
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        // Chuyển DTO sang Entity
        User user = userMapper.userDTOToUserEntity(userDTO);

        // Mã hóa mật khẩu
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));

        // Gán giá trị mặc định
        user.setRole(User.Role.CANDIDATE);
        user.setStatus(User.Status.ACTIVE);
        user.setVerified(false);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Lưu vào DB
        User savedUser = userRepository.save(user);

        // Trả về DTO (ẩn mật khẩu)
        UserDTO response = userMapper.userEntityToUserDTO(savedUser);

        return response;
    }
    // Hàm Reset mật khẩu
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new IllegalArgumentException("Email không tồn tại");

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000)); // 15 phút

        userRepository.save(user);

        // Gửi email (giả sử có EmailService)
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiry().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
    public boolean validateResetToken(String token) {
        User user = userRepository.findByResetToken(token);

        if (user == null) return false;

        Timestamp now = new Timestamp(System.currentTimeMillis()); // ✅ Không lỗi
        return user.getResetTokenExpiry() != null
                && user.getResetTokenExpiry().after(now);
    }
}
