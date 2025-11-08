package org.insertcoin.insertcoin_auth_service.dtos;

public record UpdateEmployeeRequestDTO(
        String name,
        String password,
        String role,
        Boolean active
) {}