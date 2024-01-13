package com.example.highload.services.impl;

import com.example.highload.mapper.UserMapper;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import com.example.highload.repos.RoleRepository;
import com.example.highload.repos.UserRepository;
import com.example.highload.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetailsService userDetailsService() {
        return login -> userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByLoginElseNull(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    @Override
    public void deactivateById(int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setIsActual(false);
        user.setWhenDeletedTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public Page<User> findAllExpired(LocalDateTime dateTimeLTDelete, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 50);
        return userRepository.findAllByIsActualFalseAndWhenDeletedTimeLessThan(dateTimeLTDelete, pageable).orElse(Page.empty());
    }

    @Override
    public void deleteAllExpired(LocalDateTime dateTimeLTDelete) {
        userRepository.deleteAllByIsActualFalseAndWhenDeletedTimeLessThan(dateTimeLTDelete);
    }

    // TODO What is the use case here?
    @Override
    public User save(User user) {
        user.getRole().setId(roleRepository.findByName(user.getRole().getName()).orElseThrow().getId());
        user.setIsActual(true);
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElseThrow();
    }


}
