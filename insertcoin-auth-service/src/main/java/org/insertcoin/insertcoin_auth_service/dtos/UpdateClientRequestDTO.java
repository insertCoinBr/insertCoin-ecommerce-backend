package org.insertcoin.insertcoin_auth_service.dtos;

public record UpdateClientRequestDTO(
        String name,
        Integer points,
        Boolean active
) {}
