package org.insertcoin.productservice.dtos;

import java.util.List;

public record EditProductRequestDTO(
        String name,
        String gameId,
        double price,
        List<String> category,
        String platform,
        String description,
        String img
) {}