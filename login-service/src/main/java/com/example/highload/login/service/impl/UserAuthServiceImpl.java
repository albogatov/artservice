package com.example.highload.login.service.impl;

import com.example.highload.login.repos.UserRepository;
import com.example.highload.login.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return login -> userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
