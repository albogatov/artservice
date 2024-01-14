package com.example.highload.login.controller;

import com.example.highload.login.model.network.JwtResponse;
import com.example.highload.login.model.network.UserDto;
import com.example.highload.login.security.util.JwtTokenUtil;
import com.example.highload.login.service.LoginService;
import com.example.highload.login.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final UserAuthService userAuthService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto user) {
        if (user.getLogin() == null || user.getPassword() == null) {
            return new ResponseEntity<>("Absent login or password", HttpStatus.BAD_REQUEST);
        }
        String jwt = loginService.login(user.getLogin(), user.getPassword(), user.getRole().toString());
        JwtResponse response = JwtResponse.builder().token(jwt)
                .userId(loginService.findByLoginElseNull(user.getLogin()).getId()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/details")
    public ResponseEntity<?> details(@Valid @RequestBody UserDto user) {
        return ResponseEntity.ok(userAuthService.userDetailsService());
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody String token) {
        return ResponseEntity.ok(jwtTokenUtil.validateToken(token));
    }

    @PostMapping("/get-login-from-token")
    public ResponseEntity<String> getLoginFromToken(@RequestBody String token) {
        return ResponseEntity.ok(jwtTokenUtil.getLoginFromToken(token));
    }
}
