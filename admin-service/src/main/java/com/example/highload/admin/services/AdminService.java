package com.example.highload.admin.services;

import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;

public interface AdminService {


    User addUser(UserDto userDto);

    void deleteUser(int userId);

    void deleteLogicallyDeletedUsers(int daysToExpire);

}
