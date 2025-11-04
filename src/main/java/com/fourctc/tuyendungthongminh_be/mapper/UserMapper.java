package com.fourctc.tuyendungthongminh_be.mapper;

import com.fourctc.tuyendungthongminh_be.dto.UserDTO;
import com.fourctc.tuyendungthongminh_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Để Spring tự động inject Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mappings({
            @Mapping(source = "role", target = "role", qualifiedByName = "roleToString"),
            @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    })

    // Ánh xạ từ UserEntity sang UserDTO
    UserDTO userEntityToUserDTO(User user);
    @Mappings({
            @Mapping(target = "role", expression = "java(User.Role.valueOf(userDTO.getRole() != null ? userDTO.getRole() : \"CANDIDATE\"))"),
            @Mapping(target = "status", expression = "java(User.Status.valueOf(userDTO.getStatus() != null ? userDTO.getStatus() : \"ACTIVE\"))"),
            @Mapping(target = "passwordHash", ignore = true), // password xử lý riêng trong service
            @Mapping(target = "verificationToken", ignore = true),
            @Mapping(target = "resetToken", ignore = true),
            @Mapping(target = "resetTokenExpiry", ignore = true)
    })
    // Ánh xạ từ UserDTO sang UserEntity
    User userDTOToUserEntity(UserDTO userDTO);

    @Named("roleToString")
    default String roleToString(User.Role role) {
        return role != null ? role.name() : null;
    }

    @Named("statusToString")
    default String statusToString(User.Status status) {
        return status != null ? status.name() : null;
    }
}
