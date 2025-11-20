package org.insertcoin.insertcoin_auth_service.dtos;

import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        boolean active,
        Integer point,
        Set<String> roles
) {}
