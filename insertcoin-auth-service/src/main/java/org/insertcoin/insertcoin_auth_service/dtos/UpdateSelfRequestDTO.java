package org.insertcoin.insertcoin_auth_service.dtos;

public record UpdateSelfRequestDTO(
        String name,
        String currentPassword,
        String newPassword,
        Boolean active
) {}