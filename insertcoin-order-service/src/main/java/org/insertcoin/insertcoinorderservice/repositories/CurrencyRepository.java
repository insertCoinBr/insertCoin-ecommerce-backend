package org.insertcoin.insertcoinorderservice.repositories;

import org.insertcoin.insertcoinorderservice.entities.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    CurrencyEntity findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);

}
