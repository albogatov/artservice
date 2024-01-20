package com.example.highload.admin.controller;

import com.example.highload.admin.model.network.UserDto;
import com.example.highload.admin.services.AdminService;
import com.example.highload.admin.services.AdminUserService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(description = "Delete user",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            examples = {@ExampleObject(value = "User deleted")}
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect", content = {
                    @Content(
                            examples = {@ExampleObject(value = "Wrong ids or parameters in path!")}
                    )
            }),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader(value = "Authorization") String token) {
        adminService.deleteUser(id, token);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/all/delete-expired/{days}")
    @Operation(description = "Delete logically expired users",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            examples = {@ExampleObject(value = "Users deleted")}
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect", content = {
                    @Content(
                            examples = {@ExampleObject(value = "Request body validation failed! Issues with <incorrect fields>")}
                    )
            }),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<?> deleteLogicallyDeletedAccountsExpired(@PathVariable int days, @RequestHeader(value = "Authorization") String token) {
        adminService.deleteLogicallyDeletedUsers(days, token);
        return ResponseEntity.ok("Users deleted");
    }

    @PostMapping("/add")
    @Operation(description = "Add new user",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            examples = {@ExampleObject(value = "User added")}
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect", content = {
                    @Content(
                            examples = {@ExampleObject(value = "Request body validation failed! Issues with <incorrect fields>")}
                    )
            }),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
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
        return ResponseEntity.badRequest().body("Wrong ids or parameters in path!");
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
