package org.insertcoin.currencyservice.services;

import org.insertcoin.currencyservice.clients.CurrencyBCClient;
import org.insertcoin.currencyservice.clients.CurrencyBCResponse;
import org.insertcoin.currencyservice.entities.CurrencyEntity;
import org.insertcoin.currencyservice.repositories.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CurrencyService {

    private final CurrencyRepository repository;
    private final CurrencyBCClient client;

    public CurrencyService(CurrencyRepository repository, CurrencyBCClient client) {
        this.repository = repository;
        this.client = client;
    }

    @Transactional
    public CurrencyEntity getConversion(String source, String target) {
        Optional<CurrencyEntity> existing = repository.findBySourceAndTarget(source, target);

        if (existing.isPresent()) {
            return existing.get();
        }

        // Chama o Banco Central
        CurrencyBCResponse response = client.getCurrencyBC(source);
        if (response.getValue().isEmpty()) {
            throw new RuntimeException("Cotação não encontrada no Banco Central");
        }

        double cotacaoVenda = response.getValue().get(0).getCotacaoVenda();

        // Salva no banco
        CurrencyEntity entity = new CurrencyEntity();
        entity.setSource(source);
        entity.setTarget(target);
        entity.setConversionRate(cotacaoVenda);
        repository.save(entity);

        return entity;
    }
}
