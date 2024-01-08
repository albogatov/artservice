package com.example.highload.admin.services.impl;

import com.example.highload.admin.feign.UserServiceFeignClient;
import com.example.highload.admin.mapper.UserMapper;
import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final UserServiceFeignClient userServiceFeignClient;

    public User findByLoginElseNull(String login) {
        return userMapper.userDtoToUser(userServiceFeignClient.findByLoginElseNull(login).getBody());
    }

    @Override
    public User save(User user) {
        return userMapper.userDtoToUser(userServiceFeignClient.saveUser(userMapper.userToDto(user)).getBody());
    }

    @Override
    public void deleteById(Integer id) {
        userServiceFeignClient.deleteUser(id).getBody();
    }

    @Override
    public User findById(int id) {
        return userMapper.userDtoToUser(userServiceFeignClient.findById(id).getBody());
    }


}
