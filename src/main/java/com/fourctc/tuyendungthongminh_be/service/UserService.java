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
}
