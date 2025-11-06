package org.insertcoin.insertcoin_auth_service.controllers;


import org.insertcoin.insertcoin_auth_service.dtos.AdminSignupDTO;
import org.insertcoin.insertcoin_auth_service.dtos.UpdatePasswordRequestDTO;
import org.insertcoin.insertcoin_auth_service.dtos.UpdatePasswordResponseDTO;
import org.insertcoin.insertcoin_auth_service.dtos.UserResponseDTO;
import org.insertcoin.insertcoin_auth_service.entities.RoleEntity;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('MANAGER_STORE')")
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> createAdminUser(
            @RequestBody AdminSignupDTO dto
    ) throws Exception {

        String normalizedRole = dto.role() == null ? "" : dto.role().toUpperCase().replace("ROLE_", "");
        if (!normalizedRole.equals("MANAGER_STORE") && !normalizedRole.equals("COMMERCIAL")) {
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

    @PreAuthorize("hasRole('MANAGER_STORE') and hasAuthority('EMPLOYEE')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable UUID id
    ) {
        UserResponseDTO userResponse = userService.findUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("hasRole('MANAGER_STORE')")
    @PostMapping("/reset-password")
    public ResponseEntity<UpdatePasswordResponseDTO> adminResetPassword(
            @RequestBody UpdatePasswordRequestDTO request
    ) {
        String email = request.email();
        String newPassword = request.newPassword();

        try {
            boolean updated = userService.updatePassword(email, newPassword);

            if (!updated) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new UpdatePasswordResponseDTO(
                                "User not found.",
                                email
                        ));
            }

            return ResponseEntity.ok(new UpdatePasswordResponseDTO(
                    "Password reset successfully by admin.",
                    email
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UpdatePasswordResponseDTO(
                            "Error resetting password.",
                            email
                    ));
        }
    }

}
