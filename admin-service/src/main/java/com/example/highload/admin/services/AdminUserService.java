package com.example.highload.admin.services;

import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;

public interface AdminUserService {

    User findByLoginElseNull(String login);

    User findById(int id);

    User save(User user);

    void deleteById(Integer id);
}
