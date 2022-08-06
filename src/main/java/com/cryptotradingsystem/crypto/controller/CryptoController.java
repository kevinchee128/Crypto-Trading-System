package com.cryptotradingsystem.crypto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotradingsystem.crypto.dto.CryptoCurrencyDTO;
import com.cryptotradingsystem.crypto.service.CryptoService;

@RestController
@RequestMapping("/v1/price")
public class CryptoController {

    @Autowired
    CryptoService cryptoService;
    
    @GetMapping
    public ResponseEntity<List<CryptoCurrencyDTO>> getBestAggregatedPrice()
    {
        List<CryptoCurrencyDTO> result = cryptoService.getAllPrice();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CryptoCurrencyDTO> getPriceBySymbol(@PathVariable String symbol) 
    {
        if(symbol.isEmpty() || symbol == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CryptoCurrencyDTO result = cryptoService.getPriceBySymbol(symbol);

        if(result == null)
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
