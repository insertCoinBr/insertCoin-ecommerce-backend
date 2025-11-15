package org.insertcoin.productservice.services;

import org.insertcoin.productservice.clients.CurrencyClient;
import org.insertcoin.productservice.clients.CurrencyResponse;
import org.insertcoin.productservice.dtos.AddProductRequestDTO;
import org.insertcoin.productservice.dtos.EditProductRequestDTO;
import org.insertcoin.productservice.dtos.ProductResponseDTO;
import org.insertcoin.productservice.entities.CategoryEntity;
import org.insertcoin.productservice.entities.ProductEntity;
import org.insertcoin.productservice.entities.ProductRatingEntity;
import org.insertcoin.productservice.repositories.CategoryRepository;
import org.insertcoin.productservice.repositories.PlatformRepository;
import org.insertcoin.productservice.repositories.ProductRatingRepository;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CurrencyClient currencyClient;
    private final CategoryRepository categoryRepository;
    private final ProductRatingRepository ratingRepository;
    private final PlatformRepository platformRepository;

    public ProductService(
            ProductRepository repository,
            CurrencyClient currencyClient,
            CategoryRepository categoryRepository,
            ProductRatingRepository ratingRepository,
            PlatformRepository platformRepository
    ) {
        this.repository = repository;
        this.currencyClient = currencyClient;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.platformRepository = platformRepository;
    }

    // ====================================================
    // CURRENCY
    // ====================================================
    public List<ProductEntity> findAllWithCurrency(String targetCurrency) {
        List<ProductEntity> products = repository.findAll();

        if (!targetCurrency.equalsIgnoreCase("BRL")) {
            for (ProductEntity product : products) {
                double converted = convertPrice(product.getPrice(), "BRL", targetCurrency);
                product.setPrice(converted);
            }
        }

        return products;
    }

    public double convertPrice(double price, String source, String target) {
        try {
            CurrencyResponse response = currencyClient.convert(price, source, target);
            if (response != null) {
                double converted = response.getConvertedValue();
                return BigDecimal.valueOf(converted)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
        } catch (Exception e) {
            System.err.println("Erro ao converter moeda: " + e.getMessage());
        }

        return BigDecimal.valueOf(price)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // ====================================================
    // RATINGS
    // ====================================================
    @Transactional
    public ProductEntity addRating(String gameId, double ratingValue) {

        var product = repository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        var rating = new ProductRatingEntity();
        rating.setProduct(product);
        rating.setRating(ratingValue);
        ratingRepository.save(rating);

        var allRatings = ratingRepository.findByProductId(product.getId());

        double avg = allRatings.stream()
                .mapToDouble(ProductRatingEntity::getRating)
                .average()
                .orElse(0.0);

        product.setRating(avg);
        return repository.save(product);
    }

    // ====================================================
    // GET ONE PRODUCT
    // ====================================================
    public ProductResponseDTO findOneWithCurrency(UUID id, String targetCurrency) {

        var product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        var categories = product.getCategories();

        var platform = platformRepository.findById(product.getPlatform().getId())
                .orElseThrow(() -> new RuntimeException("Plataforma não encontrada"));

        var ratings = ratingRepository.findByProductId(product.getId());

        double avg = ratings.stream()
                .mapToDouble(ProductRatingEntity::getRating)
                .average()
                .orElse(0.0);

        product.setRating(avg);

        double finalPrice = product.getPrice();
        if (targetCurrency != null && !targetCurrency.isBlank()) {
            finalPrice = convertPrice(
                    product.getPrice(),
                    "BRL",
                    targetCurrency.toUpperCase()
            );
        }

        return ProductResponseDTO.from(product, categories, platform, finalPrice);
    }

    // ====================================================
    // CREATE PRODUCT
    // ====================================================
    @Transactional
    public ProductResponseDTO createProduct(AddProductRequestDTO dto) {

        var platform = platformRepository.findByName(dto.platform())
                .orElseThrow(() -> new RuntimeException("Plataforma não encontrada"));

        long nextSeq = repository.countByPlatform(platform) + 1;
        String gameId = platform.getName().toUpperCase() + "_" + String.format("%03d", nextSeq);

        var p = new ProductEntity();
        p.setGameId(gameId);
        p.setName(dto.name());
        p.setPrice(dto.price());
        p.setDescription(dto.description());
        p.setImageUrl(dto.img());
        p.setPlatform(platform);

        var saved = repository.save(p);

        for (String cName : dto.category()) {
            var category = categoryRepository.findByName(cName)
                    .orElseThrow(() -> new RuntimeException("Categoria inválida: " + cName));
            repository.insertCategory(saved.getId(), category.getId());
        }

        var categories = categoryRepository.findByProductId(saved.getId());

        return ProductResponseDTO.from(saved, categories, platform, saved.getPrice());
    }

    // ====================================================
    // DELETE PRODUCT
    // ====================================================
    public void deleteProduct(UUID id, String gameId) {
        var existing = repository.findByIdAndGameId(id, gameId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        repository.delete(existing);
    }

    // ====================================================
    // UPDATE PRODUCT
    // ====================================================
    @Transactional
    public ProductResponseDTO updateProduct(UUID id, EditProductRequestDTO dto) {

        var product = repository.findByIdAndGameId(id, dto.gameId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setDescription(dto.description());
        product.setImageUrl(dto.img());

        var platform = platformRepository.findByName(dto.platform())
                .orElseThrow(() -> new RuntimeException("Plataforma inválida"));

        product.setPlatform(platform);

        var updated = repository.save(product);

        repository.deleteCategories(updated.getId());
        for (String c : dto.category()) {
            var cat = categoryRepository.findByName(c)
                    .orElseThrow(() -> new RuntimeException("Categoria inválida"));
            repository.insertCategory(updated.getId(), cat.getId());
        }

        var categories = categoryRepository.findByProductId(updated.getId());

        return ProductResponseDTO.from(updated, categories, platform, updated.getPrice());
    }
}
