package com.example.highload.controller;

import com.example.highload.mapper.UserMapper;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
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
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

    @GetMapping("/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login) {
        return ResponseEntity.ok(userMapper.userToDto(userService.findByLoginElseNull(login)));
    }

    @GetMapping("/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id) {
        return ResponseEntity.ok(userMapper.userToDto(userService.findById(id)));
    }

    // TODO What is the use case here?
    @PostMapping("/save")
    ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        User user = userService.save(userMapper.userDtoToUser(userDto));
        return ResponseEntity.ok(userMapper.userToDto(user));
    }

    @PostMapping("/deleteId/{id}")
    ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

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
