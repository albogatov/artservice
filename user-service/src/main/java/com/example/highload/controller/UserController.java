package com.example.highload.controller;

import com.example.highload.mapper.UserMapper;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import com.example.highload.services.UserService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(description = "Find user by login",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login) {
        return ResponseEntity.ok(userMapper.userToDto(userService.findByLoginElseNull(login)));
    }

    @GetMapping("/findId/{id}")
    @Operation(description = "Find user by id",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    ResponseEntity<UserDto> findById(@PathVariable int id) {
        return ResponseEntity.ok(userMapper.userToDto(userService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    @Operation(description = "Save user",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    ResponseEntity<?> saveUser(@RequestBody UserDto userDto) {
        if (userService.findByLoginElseNull(userDto.getLogin()) != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }
        User user = userService.save(userMapper.userDtoToUser(userDto));
        return ResponseEntity.ok(userMapper.userToDto(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/deleteId/{id}")
    @Operation(description = "Delete user by id",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }


    @PostMapping("/deactivate/{id}")
    @Operation(description = "Deactivate user",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<?> deactivate(@PathVariable int id) {
        userService.deactivateById(id);
        return new ResponseEntity<>("Profile deactivated", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/findExpired/{daysToExpire}/{page}")
    @Operation(description = "Find expired users",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<?> findExpired(@PathVariable int daysToExpire, @PathVariable int page) {
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(daysToExpire);
        return ResponseEntity.ok(userMapper.userListToDtoList(userService.findAllExpired(expiryTime, page).getContent()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/deleteAllExpired/{daysToExpire}")
    @Operation(description = "Delete expired users",
            tags = "Admin Only",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
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
