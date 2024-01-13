package com.example.highload.admin.services;

import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;

public interface AdminService {


    User addUser(UserDto userDto, String token);

    void deleteUser(int userId, String token);

    void deleteLogicallyDeletedUsers(int daysToExpir, String token);

}
