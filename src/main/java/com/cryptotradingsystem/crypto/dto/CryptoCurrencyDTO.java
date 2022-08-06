package com.cryptotradingsystem.crypto.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CryptoCurrencyDTO {
    
    private String symbol;
    private BigDecimal askPrice;
    private BigDecimal bidPrice;
}
