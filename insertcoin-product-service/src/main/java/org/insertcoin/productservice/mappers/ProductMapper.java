package org.insertcoin.productservice.mappers;

import org.insertcoin.productservice.dtos.AddProductRequestDTO;
import org.insertcoin.productservice.dtos.ProductResponseDTO;
import org.insertcoin.productservice.entities.CategoryEntity;
import org.insertcoin.productservice.entities.PlatformEntity;
import org.insertcoin.productservice.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "platform", ignore = true)
    @Mapping(target = "imageUrl", expression = "java(dto.img())")
    @Mapping(target = "categories", expression = "java(toCategoryEntities(dto.category()))")
    @Mapping(target = "gameId", ignore = true)
    ProductEntity toEntity(AddProductRequestDTO dto);

    // Entity -> Response DTO
    @Mapping(source = "id", target = "uuid")
    @Mapping(source = "gameId", target = "gameId")
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

    default String toPlatformName(PlatformEntity platform) {
        return platform != null ? platform.getName() : null;
    }
}
