package com.cryptotradingsystem.trade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

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

@Entity
public class Transaction {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;
    @NotNull
    private String symbol;
    @NotNull
    private BigDecimal openPrice;
    @NotNull
    private LocalDateTime openDateTime;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private BigDecimal closePrice;
    private LocalDateTime closeDateTime;
}
