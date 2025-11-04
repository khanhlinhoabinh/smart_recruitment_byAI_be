package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.mapper.UserMapper;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Đăng ký người dùng mới
     */
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

        // Sinh token xác minh email và thời gian hết hạn (24h)
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);

        // Lưu vào DB
        User savedUser = userRepository.save(user);

        // Gửi email xác minh
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        // Trả về DTO (ẩn mật khẩu)
        return userMapper.userEntityToUserDTO(savedUser);
    }

    /**
     * Xác minh email bằng token
     */
    public String verifyEmail(String token) {
        Optional<User> optionalUser = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getVerificationToken()))
                .findFirst();

        if (optionalUser.isEmpty()) {
            return "Token không hợp lệ hoặc đã được sử dụng";
        }

        User user = optionalUser.get();

        // Cập nhật trạng thái xác minh
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Xác minh email thành công! Bạn có thể đăng nhập.";
    }
}
