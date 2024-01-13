package com.example.highload.admin.services;

import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;

public interface AdminUserService {

    User findByLoginElseNull(String login, String token);

    User findById(int id, String token);

    User save(User user, String token);

    void deleteById(Integer id, String token);
}
