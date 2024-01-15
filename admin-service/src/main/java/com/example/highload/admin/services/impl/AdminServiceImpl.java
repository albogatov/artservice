package com.example.highload.admin.services.impl;

import com.example.highload.admin.feign.ImageServiceFeignClient;
import com.example.highload.admin.feign.OrderServiceFeignClient;
import com.example.highload.admin.feign.UserServiceFeignClient;
import com.example.highload.admin.mapper.UserMapper;
import com.example.highload.admin.model.inner.ClientOrder;
import com.example.highload.admin.model.inner.Profile;
import com.example.highload.admin.model.inner.User;
import com.example.highload.admin.model.network.OrderDto;
import com.example.highload.admin.model.network.UserDto;
import com.example.highload.admin.services.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserServiceFeignClient userServiceFeignClient;
    private final OrderServiceFeignClient orderServiceFeignClient;
    private final ImageServiceFeignClient imageService;
    private final AdminUserServiceImpl adminUserService;
    private final UserMapper userMapper;
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Exception.class})
    public void deleteLogicallyDeletedUsers(int daysToExpire, String token) {

        List<UserDto> usersToDelete;
        int i = 0;
        do {
            usersToDelete = userServiceFeignClient.findExpired(daysToExpire, i, token).getBody();
            for (UserDto user :
                    usersToDelete) {

                if (user.getProfileId() != null) {
                    imageService.removeAllImagesForProfile(user.getProfileId(), token);
                }

                List<OrderDto> orders = orderServiceFeignClient.getAllUserOrders(user.getId(), token).getBody();
                if (!orders.isEmpty())
                    orders.forEach(order -> {
                        imageService.removeAllImagesForOrder(order.getId(), token);
                    });
            }
            i++;
        } while (usersToDelete.size() > 0);

        userServiceFeignClient.deleteAllExpired(daysToExpire, token);

    }

    @Override
    public User addUser(UserDto userDto, String token) {
        User user = userMapper.userDtoToUser(userDto);
        user.setHashPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        return adminUserService.save(user, token);
    }

    @Override
    public void deleteUser(int userId, String token) {
        User user = adminUserService.findById(userId, token);
        adminUserService.deleteById(user.getId(), token);
    }
}
