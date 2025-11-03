package org.insertcoin.insertcoin_auth_service.controllers;

import org.insertcoin.insertcoin_auth_service.components.CustomUserDetails;
import org.insertcoin.insertcoin_auth_service.dtos.UserResponseDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import org.insertcoin.insertcoin_auth_service.components.JwtUtil;
import org.insertcoin.insertcoin_auth_service.dtos.SigninDTO;
import org.insertcoin.insertcoin_auth_service.dtos.SigninResponseDTO;
import org.insertcoin.insertcoin_auth_service.dtos.SignupDTO;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.services.UserService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationConfiguration authConfig;

    public AuthController(UserService userService, AuthenticationConfiguration authConfig) {
        this.userService = userService;
        this.authConfig = authConfig;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody SignupDTO dto) throws Exception {
        UserEntity user = userService.createUser(dto);
        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDTO> signin(@RequestBody SigninDTO dto) throws Exception {
        userService.authenticate(dto.email(), dto.password());
        String token = userService.generateJwtToken(dto.email());
        UserEntity user = userService.findByEmail(dto.email());

        return ResponseEntity.ok(new SigninResponseDTO(token));
    }
}
