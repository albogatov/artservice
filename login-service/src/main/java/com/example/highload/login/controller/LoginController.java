package com.example.highload.login.controller;

import com.example.highload.login.model.network.JwtResponse;
import com.example.highload.login.model.network.UserDto;
import com.example.highload.login.security.util.JwtTokenUtil;
import com.example.highload.login.service.LoginService;
import com.example.highload.login.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(description = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            schema = @Schema (implementation = JwtResponse.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect", content = {
                    @Content(
                            examples = {@ExampleObject(value = "Request body validation failed! Exception reading parameter <localized message>")}
                    )
            }),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
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
    @Operation(description = "Validate JWT token",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<Boolean> validateToken(@RequestBody String token) {
        return ResponseEntity.ok(jwtTokenUtil.validateToken(token));
    }

    @PostMapping("/get-login-from-token")
    @Operation(description = "Get username from JWT token",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<String> getLoginFromToken(@RequestBody String token) {
        return ResponseEntity.ok(jwtTokenUtil.getLoginFromToken(token));
    }
}
