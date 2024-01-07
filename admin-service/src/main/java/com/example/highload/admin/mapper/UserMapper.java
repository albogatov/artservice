package com.example.highload.admin.mapper;

import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);
}
