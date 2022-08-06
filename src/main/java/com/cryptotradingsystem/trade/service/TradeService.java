package com.cryptotradingsystem.trade.service;

import com.cryptotradingsystem.trade.dto.OrderDTO;
import com.cryptotradingsystem.trade.request.CloseOrderRequest;
import com.cryptotradingsystem.trade.request.OrderRequest;

public interface TradeService {
    
    public OrderDTO placeOrder(OrderRequest orderRequest);

    public void closeOrder(CloseOrderRequest closeOrderRequest);

}
