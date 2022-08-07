package com.cryptotradingsystem.trade.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotradingsystem.common.Constants;
import com.cryptotradingsystem.trade.dto.OrderDTO;
import com.cryptotradingsystem.trade.request.CloseOrderRequest;
import com.cryptotradingsystem.trade.request.OrderRequest;
import com.cryptotradingsystem.trade.service.TradeService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(Constants.V1 + "/trade")
public class TradeController {

    @Autowired
    TradeService tradeService;
    
    @PostMapping("/place-order")
    public ResponseEntity<OrderDTO> postPlaceOrder(@RequestBody @Valid OrderRequest orderRequest)
    {
        log.info("API Called: postPlaceOrder");
        OrderDTO result = tradeService.placeOrder(orderRequest);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/close-order")
    public ResponseEntity<Object> postCloseOrder(@RequestBody @Valid CloseOrderRequest closeOrderRequest)
    {
        log.info("API Called: postCloseOrder");
        tradeService.closeOrder(closeOrderRequest);

        return new ResponseEntity<>(HttpStatus.OK);    
    }
}
