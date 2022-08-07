package com.cryptotradingsystem.trade.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

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

public class CloseOrderRequest {
    
    @NotNull (message = "User Id can not be empty")
    private Long userId;
    @NotNull (message = "Order Id can not be empty")
    private Long orderId;
    @NotNull (message = "Price can not be empty")
    private BigDecimal price;
}
