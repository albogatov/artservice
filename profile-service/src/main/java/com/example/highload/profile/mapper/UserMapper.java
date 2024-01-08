package com.example.highload.profile.mapper;

import com.example.highload.profile.model.inner.User;
import com.example.highload.profile.model.network.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);
}
