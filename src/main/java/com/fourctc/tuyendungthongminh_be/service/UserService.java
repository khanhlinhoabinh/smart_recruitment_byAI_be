package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.mapper.UserMapper;
import com.fourctc.tuyendungthongminh_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;  // Giả sử bạn có UserRepository cho CRUD

    @Autowired
    private UserMapper userMapper;  // Tiêm vào UserMapper

}
