package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.LoginRequest;
import com.fourctc.tuyendungthongminh_be.dto.LoginResponse;
import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.mapper.UserMapper;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import com.fourctc.tuyendungthongminh_be.security.JwtUtil;
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
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Đăng ký người dùng mới
     */
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        User user = userMapper.userDTOToUserEntity(userDTO);
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(User.Role.CANDIDATE);
        user.setStatus(User.Status.ACTIVE);
        user.setVerified(false);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Sinh token xác minh email (UUID) – hết hạn 24h
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);

        // Lưu vào DB
        User savedUser = userRepository.save(user);

        // Gửi email xác minh
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        return userMapper.userEntityToUserDTO(savedUser);
    }

    /**
     * Đăng nhập người dùng
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }

        long accessTokenExpiry = 15 * 60 * 1000; // 15 phút
        long refreshTokenExpiry = request.isRememberMe() ? 30L * 24 * 60 * 60 * 1000 : 7L * 24 * 60 * 60 * 1000;

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), accessTokenExpiry);
        String refreshToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), refreshTokenExpiry);

        UserDTO userDTO = userMapper.userEntityToUserDTO(user);
        return new LoginResponse(accessToken, refreshToken, userDTO);
    }

    /**
     * Yêu cầu đặt lại mật khẩu (Reset Password)
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new IllegalArgumentException("Email không tồn tại");

        // Tạo token UUID ngẫu nhiên
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000)); // 15 phút

        userRepository.save(user);

        // Log ra token để dễ debug (xem đúng link chưa)
        System.out.println("📧 Reset token for " + email + ": " + token);

        // Gửi email đặt lại mật khẩu
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    /**
     * Đặt lại mật khẩu bằng token
     */
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);

        if (user == null) {
            throw new IllegalArgumentException("Token không hợp lệ");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().before(now)) {
            throw new IllegalArgumentException("Token đã hết hạn");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    /**
     * Kiểm tra tính hợp lệ của token reset mật khẩu
     */
    public boolean validateResetToken(String token) {
        User user = userRepository.findByResetToken(token);
        if (user == null) return false;

        Timestamp now = new Timestamp(System.currentTimeMillis());
        return user.getResetTokenExpiry() != null && user.getResetTokenExpiry().after(now);
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

        User user = optionalUser.get();git add
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Xác minh email thành công! Bạn có thể đăng nhập.";
    }
}
