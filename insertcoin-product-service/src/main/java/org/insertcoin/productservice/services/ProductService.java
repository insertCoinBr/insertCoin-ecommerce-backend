package org.insertcoin.productservice.services;

import org.insertcoin.productservice.clients.CurrencyClient;
import org.insertcoin.productservice.clients.CurrencyResponse;
import org.insertcoin.productservice.dtos.ProductRequestDTO;
import org.insertcoin.productservice.entities.CategoryEntity;
import org.insertcoin.productservice.entities.ProductEntity;
import org.insertcoin.productservice.entities.ProductRatingEntity;
import org.insertcoin.productservice.repositories.CategoryRepository;
import org.insertcoin.productservice.repositories.ProductRatingRepository;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CurrencyClient currencyClient;
    private final CategoryRepository categoryRepository;
    private final ProductRatingRepository ratingRepository;

    public ProductService(ProductRepository repository, CurrencyClient currencyClient, CategoryRepository categoryRepository, ProductRatingRepository ratingRepository) {
        this.repository = repository;
        this.currencyClient = currencyClient;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
    }

    // ======================================================
    // MÉTODOS BÁSICOS
    // ======================================================

    public List<ProductEntity> findAll() {
        return repository.findAll();
    }

    public Optional<ProductEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public ProductEntity save(ProductEntity entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteByGameId(String gameId) {
        repository.deleteByGameId(gameId);
    }

    // ======================================================
    // NOVOS MÉTODOS
    // ======================================================

    /**
     * Retorna todos os produtos com preço convertido para a moeda desejada.
     * Exemplo: ?curr=USD
     */
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


    /**
     * Atualiza a avaliação de um produto com base no gameId.
     */
    @Transactional
    public ProductEntity updateAvaliation(String gameId, Double newRating) {
        ProductEntity entity = repository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + gameId));

        entity.setRating(newRating);
        return repository.save(entity);
    }

    /**
     * Atualiza informações do produto com base em seu gameId.
     */
    @Transactional
    public ProductEntity updateByGameId(ProductRequestDTO dto) {
        ProductEntity entity = repository.findByGameId(dto.gameId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + dto.gameId()));

        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setImageUrl(dto.img());
        entity.setPrice(dto.price());

        List<CategoryEntity> categories = dto.category().stream()
                .map(name -> categoryRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + name)))
                .collect(Collectors.toList());

        entity.setCategories(categories);
        return repository.save(entity);
    }

    /**
     * Conversão de moeda via CurrencyClient (microservice Currency).
     */
    /**
     * Conversão de moeda via CurrencyClient (microservice Currency).
     */
    public double convertPrice(double price, String source, String target) {
        try {
            CurrencyResponse response = currencyClient.convert(price, source, target);
            if (response != null) {
                double converted = response.getConvertedValue();
                // Arredonda para 2 casas decimais
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

    @Transactional
    public ProductEntity addRating(String gameId, Double newRating) {
        ProductEntity product = repository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + gameId));

        // cria novo registro de avaliação
        ProductRatingEntity ratingEntity = new ProductRatingEntity();
        ratingEntity.setProduct(product);
        ratingEntity.setRating(newRating);
        ratingRepository.save(ratingEntity);

        // recalcula média
        var ratings = ratingRepository.findByProductUuid(product.getId());
        double avg = ratings.stream().mapToDouble(ProductRatingEntity::getRating).average().orElse(0.0);

        product.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP).doubleValue());
        return repository.save(product);
    }



}
