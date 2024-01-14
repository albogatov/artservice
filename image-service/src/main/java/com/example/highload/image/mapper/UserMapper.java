package com.example.highload.image.mapper;

import com.example.highload.image.model.enums.RoleType;
import com.example.highload.image.model.inner.Role;
import com.example.highload.image.model.inner.User;
import com.example.highload.image.model.network.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    @Mapping(source = "password", target = "hashPassword")
    User userDtoToUser(UserDto userDto);

    default RoleType map(Role role) {
        return role.getName();
    }

    default Role map(RoleType roleType) {
        Role role = new Role();
        role.setName(roleType);
        return role;
    }
}
