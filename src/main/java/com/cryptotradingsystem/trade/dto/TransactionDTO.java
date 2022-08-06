package com.cryptotradingsystem.trade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cryptotradingsystem.common.Constants.OrderType;
import com.cryptotradingsystem.common.Constants.Status;

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

public class TransactionDTO {
    
    private Long userId;
    private String symbol;
    private BigDecimal price;
    private OrderType orderType;
    private LocalDateTime dateTime;
    private Status status;
    private BigDecimal amount;
}
