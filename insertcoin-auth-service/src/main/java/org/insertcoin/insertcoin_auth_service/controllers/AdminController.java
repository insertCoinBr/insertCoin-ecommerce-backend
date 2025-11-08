package org.insertcoin.insertcoin_auth_service.controllers;


import org.insertcoin.insertcoin_auth_service.dtos.*;
import org.insertcoin.insertcoin_auth_service.entities.RoleEntity;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.services.UserService;
import org.springframework.data.domain.Page;
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

    @PreAuthorize("hasRole('MANAGER_STORE') and hasAuthority('EMPLOYEES_ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable UUID id
    ) {
        UserResponseDTO userResponse = userService.findUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("hasRole('MANAGER_STORE')")
    @PutMapping("/employees/update/{id}")
    public ResponseEntity<UpdateEmployeeResponseDTO> updateEmployee(
            @PathVariable UUID id,
            @RequestBody UpdateEmployeeRequestDTO request
    ) {
        try {
            boolean updated = userService.updateEmployee(id, request);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UpdateEmployeeResponseDTO("Employee not found."));
            }

            return ResponseEntity.ok(
                    new UpdateEmployeeResponseDTO("Employee updated successfully.")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UpdateEmployeeResponseDTO("Error updating employee."));
        }
    }

    @PreAuthorize("hasAuthority('EMPLOYEES_ADMIN')")
    @GetMapping("/employees/search")
    public ResponseEntity<Page<UserEmailDTO>> searchEmployees(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserEmailDTO> result = userService.searchEmployees(email, page, size);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('CLIENTS_ADMIN')")
    @GetMapping("/clients/search")
    public ResponseEntity<Page<UserEmailDTO>> searchClients(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserEmailDTO> result = userService.searchClient(email, page, size);
        return ResponseEntity.ok(result);
    }

}
