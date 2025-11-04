package org.insertcoin.insertcoin_auth_service.dtos;

public record UpdatePasswordRequestDTO(
        String email,
        String newPassword
) {}
