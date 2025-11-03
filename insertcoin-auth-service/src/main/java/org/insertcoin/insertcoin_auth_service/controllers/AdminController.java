package org.insertcoin.insertcoin_auth_service.controllers;


import org.insertcoin.insertcoin_auth_service.dtos.AdminSignupDTO;
import org.insertcoin.insertcoin_auth_service.dtos.UserResponseDTO;
import org.insertcoin.insertcoin_auth_service.entities.RoleEntity;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> createAdminUser(
            @RequestBody AdminSignupDTO dto
    ) throws Exception {

        String normalizedRole = dto.role() == null ? "" : dto.role().toUpperCase().replace("ROLE_", "");
        if (!normalizedRole.equals("SUPER_ADMIN") && !normalizedRole.equals("MANAGER_STORE")) {
            return ResponseEntity.badRequest().build();
        }

        UserEntity user = userService.createAdminUser(dto.name(), dto.email(), dto.password(), normalizedRole);

        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
