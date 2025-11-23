package org.insertcoin.productservice.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.insertcoin.productservice.dtos.ProductKeyImportResponseDTO;
import org.insertcoin.productservice.entities.ProductKeyEntity;
import org.insertcoin.productservice.repositories.ProductKeyRepository;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

@Service
public class ProductKeyImportService {

    private final ProductKeyRepository productKeyRepository;
    private final ProductRepository productRepository;

    public ProductKeyImportService(ProductKeyRepository productKeyRepository,
                                   ProductRepository productRepository) {
        this.productKeyRepository = productKeyRepository;
        this.productRepository = productRepository;
    }

    public ProductKeyImportResponseDTO importCsv(MultipartFile file) throws Exception {

        int imported = 0;
        int invalid = 0;
        int duplicates = 0;

        Reader reader = new InputStreamReader(file.getInputStream());
        CSVParser parser = CSVFormat.DEFAULT
                .withHeader("id_product", "key_code")
                .withSkipHeaderRecord()
                .parse(reader);

        for (CSVRecord record : parser) {

            try {
                UUID productId = UUID.fromString(record.get("id_product"));
                String keyCode = record.get("key_code");

                // Verifica se o produto existe
                if (!productRepository.existsById(productId)) {
                    invalid++;
                    continue;
                }

                // Verifica duplicada
                if (productKeyRepository.findByKeyCode(keyCode).isPresent()) {
                    duplicates++;
                    continue;
                }

                // Salva
                ProductKeyEntity key = new ProductKeyEntity();
                key.setProductId(productId);
                key.setKeyCode(keyCode);

                productKeyRepository.save(key);
                imported++;

            } catch (Exception e) {
                invalid++;
            }
        }

        return new ProductKeyImportResponseDTO(imported, invalid, duplicates);
    }
}
