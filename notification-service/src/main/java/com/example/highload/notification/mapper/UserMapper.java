package com.example.highload.notification.mapper;

import com.example.highload.notification.model.inner.User;
import com.example.highload.notification.model.network.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);
}
