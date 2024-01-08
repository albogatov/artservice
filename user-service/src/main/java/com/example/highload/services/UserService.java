package com.example.highload.services;

import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;

public interface UserService {

    UserDetailsService userDetailsService();

    User findByLoginElseNull(String login);

    User findById(int id);

    void deactivateById(int userId);

    Page<User> findAllExpired(LocalDateTime dateTimeLTDelete, int page);

    void deleteAllExpired(LocalDateTime dateTimeLTDelete);

    User save(User user);


    void deleteById(Integer id);
}
