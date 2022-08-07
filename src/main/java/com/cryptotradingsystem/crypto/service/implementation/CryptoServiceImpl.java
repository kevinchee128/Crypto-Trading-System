package com.cryptotradingsystem.crypto.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cryptotradingsystem.crypto.dto.CryptoCurrencyDTO;
import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrencyRepository;
import com.cryptotradingsystem.crypto.service.CryptoService;

@Service
public class CryptoServiceImpl implements CryptoService {

    @Autowired
    CryptoCurrencyRepository cryptoCurrencyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CryptoCurrencyDTO> getAllPrice() 
    {
        List<CryptoCurrency> result = cryptoCurrencyRepository.findAll();

        return result.stream().map(c -> CryptoCurrencyDTO.builder()
                                                .symbol(c.getSymbol())
                                                .askPrice(c.getAskPrice())
                                                .bidPrice(c.getBidPrice())
                                                .currency(c.getCurrency())
                                                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CryptoCurrencyDTO getPriceBySymbol(String symbol) 
    {
        Optional<CryptoCurrency> result = cryptoCurrencyRepository.findBySymbolIgnoreCase(symbol);

        if(!result.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Crypto Trading Pair is not Supported");
        }

         
        return CryptoCurrencyDTO.builder()
                    .symbol(result.get().getSymbol())
                    .askPrice(result.get().getAskPrice())
                    .bidPrice(result.get().getBidPrice())
                    .currency(result.get().getCurrency())
                    .build();
    }
    
}
