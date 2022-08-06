package com.cryptotradingsystem.crypto.service;

import java.util.List;

import com.cryptotradingsystem.crypto.dto.CryptoCurrencyDTO;

public interface CryptoService {
    
    public List<CryptoCurrencyDTO> getAllPrice();

    public CryptoCurrencyDTO getPriceBySymbol(String symbol);
}
