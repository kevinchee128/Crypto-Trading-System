package com.cryptotradingsystem.user.dto;

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
@ToString
@Builder
public class WalletDTO {
    
    private Long id;
    private String currency;
    private BigDecimal balance;
    private BigDecimal currentBalance;
}
