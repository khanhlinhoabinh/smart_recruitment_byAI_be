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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        User user = userMapper.userDTOToUserEntity(userDTO);
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(User.Role.CANDIDATE);
        user.setStatus(User.Status.ACTIVE);
        user.setVerified(false);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        User savedUser = userRepository.save(user);
        return userMapper.userEntityToUserDTO(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }

        long accessTokenExpiry = 15 * 60 * 1000; // 15 phút
        long refreshTokenExpiry = request.isRememberMe() ? 30L * 24 * 60 * 60 * 1000 : 7L * 24 * 60 * 60 * 1000;

        String accessToken = jwtUtil.generateToken(user.getEmail(), accessTokenExpiry);
        String refreshToken = jwtUtil.generateToken(user.getEmail(), refreshTokenExpiry);

        UserDTO userDTO = userMapper.userEntityToUserDTO(user);
        return new LoginResponse(accessToken, refreshToken, userDTO);
    }
}