package org.insertcoin.productservice.dtos;

import java.util.List;
import java.util.UUID;

public record ProductResponseDTO(
        UUID uuid,
        String gameId,
        String name,
        Double price,
        List<String> category,
        String platform,
        String description,
        String imageUrl,
        Double rating,
        Integer stock
) {}
