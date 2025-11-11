package org.insertcoin.productservice.mappers;

import org.insertcoin.productservice.dtos.ProductRequestDTO;
import org.insertcoin.productservice.dtos.ProductResponseDTO;
import org.insertcoin.productservice.entities.CategoryEntity;
import org.insertcoin.productservice.entities.PlatformEntity;
import org.insertcoin.productservice.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Request DTO -> Entity
    @Mapping(target = "categories", expression = "java(toCategoryEntities(dto.category()))")
    ProductEntity toEntity(ProductRequestDTO dto);

    // Entity -> Response DTO
    @Mapping(source = "id", target = "uuid")
    @Mapping(target = "category", expression = "java(toCategoryNames(entity.getCategories()))")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "platform", expression = "java(toPlatformName(entity.getPlatform()))")
    @Mapping(source = "rating", target = "rating")
    ProductResponseDTO toResponseDTO(ProductEntity entity);

    // ---- Métodos auxiliares ----
    default List<String> toCategoryNames(List<CategoryEntity> categories) {
        if (categories == null) return List.of();
        return categories.stream()
                .map(CategoryEntity::getName)
                .collect(Collectors.toList());
    }

    default List<CategoryEntity> toCategoryEntities(List<String> categoryNames) {
        if (categoryNames == null) return List.of();
        return categoryNames.stream()
                .map(name -> {
                    CategoryEntity cat = new CategoryEntity();
                    cat.setName(name);
                    return cat;
                })
                .collect(Collectors.toList());
    }

    // Conversão da PlatformEntity para String
    default String toPlatformName(PlatformEntity platform) {
        return platform != null ? platform.getName() : null;
    }
}
