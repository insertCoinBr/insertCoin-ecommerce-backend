package org.insertcoin.productservice.dtos;

import java.util.List;
import java.util.UUID;

public record ProductRequestDTO(
        UUID uuid,
        String gameId,
        String name,
        Double price,
        List<String> category,
        String description,
        String img
) {}
