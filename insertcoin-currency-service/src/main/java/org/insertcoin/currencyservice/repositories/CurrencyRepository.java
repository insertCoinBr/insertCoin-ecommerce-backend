package org.insertcoin.currencyservice.repositories;

import org.insertcoin.currencyservice.entities.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    Optional<CurrencyEntity> findBySourceAndTarget(String source, String target);
}
