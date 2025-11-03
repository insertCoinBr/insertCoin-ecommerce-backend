package org.insertcoin.insertcoin_auth_service.dtos;

public record AdminSignupDTO(
        String name,
        String email,
        String password,
        String role
) {}
