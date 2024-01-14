package com.example.highload.admin.mapper;

import com.example.highload.admin.model.enums.RoleType;
import com.example.highload.admin.model.inner.Role;
import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "profile.id", target = "profileId")
    UserDto userToDto(User user);

    @Mapping(target = "profile.id", source = "profileId")
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
