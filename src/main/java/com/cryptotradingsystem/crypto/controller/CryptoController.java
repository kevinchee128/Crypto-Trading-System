package com.cryptotradingsystem.crypto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotradingsystem.common.Constants;
import com.cryptotradingsystem.crypto.dto.CryptoCurrencyDTO;
import com.cryptotradingsystem.crypto.service.CryptoService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(Constants.V1 + "/price")
public class CryptoController {

    @Autowired
    CryptoService cryptoService;
    
    @GetMapping
    public ResponseEntity<List<CryptoCurrencyDTO>> getBestAggregatedPrice()
    {
        log.info("API Called: getBestAggregatedPrice");
        List<CryptoCurrencyDTO> result = cryptoService.getAllPrice();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CryptoCurrencyDTO> getPriceBySymbol(@PathVariable String symbol) 
    {
        log.info("API Called: getPriceBySymbol");
        CryptoCurrencyDTO result = cryptoService.getPriceBySymbol(symbol);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
