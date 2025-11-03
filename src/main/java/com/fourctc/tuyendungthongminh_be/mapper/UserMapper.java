package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Ánh xạ từ UserEntity sang UserDTO
    UserDTO userEntityToUserDTO(User user);

    // Ánh xạ từ UserDTO sang UserEntity
    User userDTOToUserEntity(UserDTO userDTO);
}
