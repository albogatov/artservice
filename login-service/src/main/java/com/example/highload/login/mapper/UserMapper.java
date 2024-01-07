package com.example.highload.login.mapper;

import com.example.highload.login.model.inner.User;
import com.example.highload.login.model.network.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);
}
