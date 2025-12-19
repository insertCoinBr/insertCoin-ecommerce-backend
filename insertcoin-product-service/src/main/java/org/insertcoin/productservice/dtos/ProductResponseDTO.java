package org.insertcoin.productservice.dtos;

import org.insertcoin.productservice.entities.CategoryEntity;
import org.insertcoin.productservice.entities.PlatformEntity;
import org.insertcoin.productservice.entities.ProductEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProductResponseDTO(
        UUID uuid,
        String name,
        Double price,
        List<String> category,
        String platform,
        String description,
        String imageUrl,
        Double rating,
        Integer stock
) {
    public static ProductResponseDTO from(
            ProductEntity p,
            List<CategoryEntity> categories,
            PlatformEntity platform,
            double finalPrice
    ) {
        List<String> catNames = (categories == null) ? List.of()
                : categories.stream().map(CategoryEntity::getName).collect(Collectors.toList());

        String platformName = (platform == null) ? null : platform.getName();

        return new ProductResponseDTO(
                p.getId(),                       // uuid
                p.getName(),                     // name
                Double.valueOf(finalPrice),      // price (autobox)
                catNames,                        // category
                platformName,                    // platform
                p.getDescription(),              // description
                p.getImageUrl(),                 // imageUrl
                p.getRating(),                   // rating
                p.getStock()                     // stock
        );
    }
}
