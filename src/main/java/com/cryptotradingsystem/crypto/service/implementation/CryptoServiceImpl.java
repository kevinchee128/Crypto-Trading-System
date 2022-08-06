package com.cryptotradingsystem.crypto.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptotradingsystem.crypto.dto.CryptoCurrencyDTO;
import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrenyRepository;
import com.cryptotradingsystem.crypto.service.CryptoService;

@Service
public class CryptoServiceImpl implements CryptoService{

    @Autowired
    CryptoCurrenyRepository cryptoCurrencyRepository;

    @Override
    public List<CryptoCurrencyDTO> getAllPrice() {
        List<CryptoCurrency> result = cryptoCurrencyRepository.findAll();

        return result.stream().map(c -> CryptoCurrencyDTO.builder()
                                                .symbol(c.getSymbol())
                                                .askPrice(c.getAskPrice())
                                                .bidPrice(c.getBidPrice())
                                                .build()).collect(Collectors.toList());
    }

    @Override
    public CryptoCurrencyDTO getPriceBySymbol(String symbol) {
        
        Optional<CryptoCurrency> result = cryptoCurrencyRepository.findBySymbol(symbol);
         
        return CryptoCurrencyDTO.builder()
                    .symbol(result.get().getSymbol())
                    .askPrice(result.get().getAskPrice())
                    .bidPrice(result.get().getBidPrice())
                    .build();
}
    
}
