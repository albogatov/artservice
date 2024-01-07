package com.example.highload.admin.services.impl;

import com.example.highload.admin.feign.UserServiceFeignClient;
import com.example.highload.admin.mapper.UserMapper;
import com.example.highload.admin.model.inner.User;
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
    private final AdminUserServiceImpl adminUserService;
    private final UserMapper userMapper;
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Exception.class})
    public void deleteLogicallyDeletedUsers(int daysToExpire) {
        LocalDateTime dateTimeLTDelete = LocalDateTime.now().minusDays(daysToExpire);
        Page<UserDto> usersToDelete;
        //Pageable pageable;
        int i = 0;
        do {
            //pageable = PageRequest.of(i, 50);
            usersToDelete = userServiceFeignClient.findExpired(dateTimeLTDelete, i).getBody();
            for (UserDto user :
                    usersToDelete.getContent()) {

                //TODO Call to profile/image service
//                Profile profile = user.getProfile();
//                if (profile != null) {
//                    imageService.removeAllImagesForProfile(profile);
//                    if (profile.getImage() != null) {
//                        imageService.removeImageById(profile.getImage().getId());
//                    }
//
//                }

                //TODO Call to order/image service
//                List<ClientOrder> orders = user.getOrders();
//                if (!orders.isEmpty())
//                    orders.forEach(imageService::removeAllImagesForOrder);
            }
            i++;
        } while (usersToDelete.getContent().size() > 0);

        userServiceFeignClient.deleteAllExpired(dateTimeLTDelete);

    }

    @Override
    public User addUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        user.setHashPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        return adminUserService.save(user);
    }

    @Override
    public void deleteUser(int userId) {
        User user = adminUserService.findById(userId);
        adminUserService.deleteById(user.getId());
    }
}
