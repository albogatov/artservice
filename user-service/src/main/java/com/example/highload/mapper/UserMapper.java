package com.example.highload.mapper;

import com.example.highload.model.enums.RoleType;
import com.example.highload.model.inner.Role;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "profileId", source = "profile.id")
    UserDto userToDto(User user);

    @Mapping(source = "profileId", target = "profile.id")
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
