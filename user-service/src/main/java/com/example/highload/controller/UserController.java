package com.example.highload.controller;

import com.example.highload.mapper.UserMapper;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import com.example.highload.services.UserService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
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

    @GetMapping("/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login) {
        return ResponseEntity.ok(userMapper.userToDto(userService.findByLoginElseNull(login)));
    }

    @GetMapping("/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id) {
        return ResponseEntity.ok(userMapper.userToDto(userService.findById(id)));
    }

    @PostMapping("/save")
    ResponseEntity<?> saveUser(@RequestBody UserDto userDto) {
        if (userService.findByLoginElseNull(userDto.getLogin()) != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }
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

    @GetMapping("/findExpired/{daysToExpire}/{page}")
    public ResponseEntity<?> findExpired(@PathVariable int daysToExpire, @PathVariable int page) {
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(daysToExpire);
        return ResponseEntity.ok(userMapper.userListToDtoList(userService.findAllExpired(expiryTime, page).getContent()));
    }

    @PostMapping("/deleteAllExpired/{daysToExpire}")
    public ResponseEntity<?> deleteAllExpired(@PathVariable int daysToExpire) {
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(daysToExpire);
        userService.deleteAllExpired(expiryTime);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body("Request body validation failed! " + exception.getLocalizedMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintValidationExceptions(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body("Request body validation failed! " + exception.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

    @ExceptionHandler({CallNotPermittedException.class, FeignException.class})
    public ResponseEntity<?> handleExternalServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("External service is unavailable now!");
    }
}
