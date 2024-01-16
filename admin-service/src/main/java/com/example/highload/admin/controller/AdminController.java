package com.example.highload.admin.controller;

import com.example.highload.admin.model.network.UserDto;
import com.example.highload.admin.services.AdminService;
import com.example.highload.admin.services.AdminUserService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService userService;
    private final AdminService adminService;

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader(value = "Authorization") String token) {
        adminService.deleteUser(id, token);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/all/delete-expired/{days}")
    public ResponseEntity<?> deleteLogicallyDeletedAccountsExpired(@PathVariable int days, @RequestHeader(value = "Authorization") String token) {
        adminService.deleteLogicallyDeletedUsers(days, token);
        return ResponseEntity.ok("Users deleted");
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserDto user, @RequestHeader(value = "Authorization") String token) {
        if (userService.findByLoginElseNull(user.getLogin(), token) == null) {
            adminService.addUser(user, token);
            return ResponseEntity.ok("User added");
        }
        return ResponseEntity.badRequest().body("User already exists!");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body("Request body validation failed! Issues with " + exception.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

    @ExceptionHandler({CallNotPermittedException.class})
    public ResponseEntity<String> handleCallNotPermittedException(CallNotPermittedException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("External service is unavailable now!");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

}
