package org.insertcoin.insertcoin_auth_service.dtos;

import java.util.Set;
import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        String name,
        String email,
        boolean active,
        int point,
        Set<String> roles,
        Set<String> permissions
) {}
