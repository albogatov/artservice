package com.example.highload.mapper;

import com.example.highload.model.enums.RoleType;
import com.example.highload.model.inner.Role;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);

    default RoleType map(Role role) {
        return role.getName();
    }
}
