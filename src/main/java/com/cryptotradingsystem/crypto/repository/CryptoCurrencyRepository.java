package com.cryptotradingsystem.crypto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptotradingsystem.crypto.entity.CryptoCurrency;

public interface CryptoCurrencyRepository extends JpaRepository<CryptoCurrency, Long>{
    
    Optional<CryptoCurrency> findBySymbolIgnoreCase(String symbol);
}
