package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.User;
import com.example.highload.order.model.network.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToDto(User user);

    User userDtoToUser(UserDto userDto);
}
