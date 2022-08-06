package com.cryptotradingsystem.crypto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.crypto.entity.CryptoCurrency;

public interface CryptoCurrenyRepository extends CrudRepository<CryptoCurrency, Long>{
    
    Optional<CryptoCurrency> findBySymbol(String symbol);

    List<CryptoCurrency> findAll();
}
