package com.example.highload.image.mapper;

import com.example.highload.image.model.inner.User;
import com.example.highload.image.model.network.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);
}
