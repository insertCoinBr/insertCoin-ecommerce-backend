package org.insertcoin.insertcoin_auth_service.dtos;

public record VerifyEmailResponseDTO(
        String status,
        String email
) {}
