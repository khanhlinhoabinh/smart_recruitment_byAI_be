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

        // Xử lý role
        String roleStr = userDTO.getRole();
        if (roleStr == null || roleStr.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò (HR hoặc CANDIDATE)");
        }
        roleStr = roleStr.toUpperCase();
        if (roleStr.equals("ADMIN")) {
            throw new IllegalArgumentException("Không thể đăng ký tài khoản ADMIN");
        }
        try {
            user.setRole(User.Role.valueOf(roleStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không hợp lệ. Chỉ chấp nhận HR hoặc CANDIDATE");
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
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

        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();

        // ✅ LOGIN ADMIN TỰ DO: chỉ cần email kết thúc @admin.vn và mật khẩu = 123456
        if (email.endsWith("@admin.vn") && "123456".equals(password)) {

            User admin = userRepository.findByEmail(email);

            // Nếu admin chưa tồn tại trong DB → tạo mới
            if (admin == null) {
                admin = new User();
                admin.setEmail(email);
                admin.setFullName("Administrator");
                admin.setRole(User.Role.ADMIN);
                admin.setVerified(true);
                admin.setStatus(User.Status.ACTIVE);
                admin.setPasswordHash(passwordEncoder.encode("123456"));
                admin.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                admin = userRepository.save(admin);
            }

            long accessTokenExpiry = 15 * 60 * 1000;
            long refreshTokenExpiry = request.isRememberMe()
                    ? 30L * 24 * 60 * 60 * 1000
                    : 7L * 24 * 60 * 60 * 1000;

            String accessToken = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name(), accessTokenExpiry);
            String refreshToken = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name(), refreshTokenExpiry);

            UserDTO adminDTO = userMapper.userEntityToUserDTO(admin);

            return new LoginResponse(accessToken, refreshToken, adminDTO);
        }

        // ✅ LOGIN BÌNH THƯỜNG
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }

        long accessTokenExpiry = 15 * 60 * 1000; // 15 phút
        long refreshTokenExpiry = request.isRememberMe()
                ? 30L * 24 * 60 * 60 * 1000
                : 7L * 24 * 60 * 60 * 1000;

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

        System.out.println("📧 Reset token for " + email + ": " + token);

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

        User user = optionalUser.get();
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Xác minh email thành công! Bạn có thể đăng nhập.";
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

    }
}
