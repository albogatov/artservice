package com.example.highload.admin.controller;

import com.example.highload.admin.model.network.UserDto;
import com.example.highload.admin.services.AdminService;
import com.example.highload.admin.services.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService userService;
    private final AdminService adminService;

    @PostMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/user/all/delete-expired/{days}")
    public ResponseEntity<?> deleteLogicallyDeletedAccountsExpired(@PathVariable int days) {
        adminService.deleteLogicallyDeletedUsers(days);
        return ResponseEntity.ok("Users deleted");
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserDto user) {
        if (userService.findByLoginElseNull(user.getLogin()) == null) {
            adminService.addUser(user);
            return ResponseEntity.ok("User added");
        }
        return ResponseEntity.badRequest().body("User already exists!");
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
