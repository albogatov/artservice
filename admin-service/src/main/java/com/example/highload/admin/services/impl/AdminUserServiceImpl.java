package com.example.highload.admin.services.impl;

import com.example.highload.admin.mapper.UserMapper;
import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.UserDto;
import com.example.highload.admin.repos.UserRepository;
import com.example.highload.admin.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findByLoginElseNull(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    @Override
    public User saveUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        user.setHashPassword(userDto.getPassword());
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
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
