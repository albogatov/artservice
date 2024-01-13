package com.example.highload.order.mapper;

import com.example.highload.order.model.enums.RoleType;
import com.example.highload.order.model.inner.Role;
import com.example.highload.order.model.inner.User;
import com.example.highload.order.model.network.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

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
