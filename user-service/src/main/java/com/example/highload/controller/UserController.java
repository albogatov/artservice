package com.example.highload.controller;

import com.example.highload.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //TODO Move to login service
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody UserDto user) {
//        if (user.getLogin() == null || user.getPassword() == null) {
//            return new ResponseEntity<>("Absent login or password", HttpStatus.BAD_REQUEST);
//        }
//        JwtResponse response = JwtResponse.builder().token(authenticationService.authProcess(user.getLogin(), user.getPassword(),
//                user.getRole().toString())).userId(userService.findByLoginElseNull(user.getLogin()).getId()).build();
//        return ResponseEntity.ok(response);
//    }

    //TODO Move to profile service
//    @PostMapping("/profile/add/{userId}")
//    public ResponseEntity<?> addProfile(@Valid @RequestBody ProfileDto profile, @PathVariable int userId) {
//
//        if (profileService.findByUserIdElseNull(userId) == null) {
//            profileService.saveProfileForUser(profile, userId);
//            return new ResponseEntity<>("Profile successfully added", HttpStatus.OK);
//        }
//        return new ResponseEntity<>("Profile already added", HttpStatus.BAD_REQUEST);
//    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable int id) {
        userService.deactivateById(id);
        return new ResponseEntity<>("Profile deactivated", HttpStatus.OK);
    }

    @GetMapping("/findExpired/{expiryTime}/{page}")
    public ResponseEntity<?> findExpired(@PathVariable LocalDateTime expiryTime, @PathVariable int page) {
        return ResponseEntity.ok(userService.findAllExpired(expiryTime, page));
    }

    @PostMapping("/deleteAllExpired/{expiryTime}")
    public ResponseEntity<?> deleteAllExpired(@PathVariable LocalDateTime expiryTime) {
        userService.deleteAllExpired(expiryTime);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions() {
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

}
