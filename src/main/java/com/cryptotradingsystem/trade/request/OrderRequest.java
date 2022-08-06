package com.cryptotradingsystem.trade.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.cryptotradingsystem.common.Constants.OrderType;

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

public class OrderRequest {
    
    @NotNull (message = "User Id can not be empty")
    private Long userId;
    @NotBlank (message = "Symbol can not be empty")
    private String symbol;
    @NotNull (message = "Amount can not be empty")
    private BigDecimal amount;
    @NotNull (message = "Price can not be empty")
    private BigDecimal price;
    @NotNull (message = "Order Type can not be empty")
    private OrderType orderType;
}
