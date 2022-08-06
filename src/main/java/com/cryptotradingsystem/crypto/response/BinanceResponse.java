package com.cryptotradingsystem.crypto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BinanceResponse {
    
    private String symbol;
    private BigDecimal askPrice;
    private BigDecimal bidPrice;
}
