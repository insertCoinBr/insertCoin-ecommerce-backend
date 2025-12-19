package org.insertcoin.productservice.dtos;

import java.util.List;

public record AddProductRequestDTO(
        String name,
        double price,
        List<String> category,
        String platform,
        String description,
        String img,
        int stock
) {}
