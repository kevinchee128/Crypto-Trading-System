package com.cryptotradingsystem.trade.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cryptotradingsystem.common.Constants.OrderType;
import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrencyRepository;
import com.cryptotradingsystem.trade.dto.OrderDTO;
import com.cryptotradingsystem.trade.entity.Transaction;
import com.cryptotradingsystem.trade.repository.TransactionRepository;
import com.cryptotradingsystem.trade.request.CloseOrderRequest;
import com.cryptotradingsystem.trade.request.OrderRequest;
import com.cryptotradingsystem.trade.service.TradeService;
import com.cryptotradingsystem.user.repository.WalletRepository;
import com.cryptotradingsystem.user.service.UserService;

@Service
public class TradeServiceImpl implements TradeService{

    @Autowired
    CryptoCurrencyRepository cryptoCurrencyRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderRequest orderRequest) 
    {
        Optional<CryptoCurrency> cryptoOptional = cryptoCurrencyRepository.findBySymbolIgnoreCase(orderRequest.getSymbol());

        if(!cryptoOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Crypto Trading Pair is not Supported");
        }

        Long orderId = null;
        CryptoCurrency crypto = cryptoOptional.get();
        BigDecimal balance = userService.calculateWalletBalance(orderRequest.getUserId(), crypto.getCurrency());

        if(balance.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient wallet balance");
        }
            
        if(canPlaceOrder(orderRequest.getOrderType(), orderRequest.getPrice(), crypto))
        {
            Transaction result = transactionRepository.save(Transaction.builder()
                                            .userId(orderRequest.getUserId())
                                            .symbol(orderRequest.getSymbol())
                                            .openPrice(orderRequest.getOrderType().equals(OrderType.BUY) ? crypto.getAskPrice() : crypto.getBidPrice())
                                            .orderType(orderRequest.getOrderType())
                                            .openDateTime(LocalDateTime.now())
                                            .status(Status.OPEN)
                                            .amount(orderRequest.getAmount())
                                            .currency(crypto.getCurrency())
                                            .build());

            orderId = result.getId();
            return OrderDTO.builder()
                                .id(orderId)
                                .build();
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Order Price");
    }

    @Override
    @Transactional
    public void closeOrder(CloseOrderRequest closeOrderRequest) 
    {
        Optional<Transaction> transactionOptional = transactionRepository.findByIdAndUserIdAndStatus(closeOrderRequest.getOrderId(), closeOrderRequest.getUserId(), Status.OPEN);

        if(!transactionOptional.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Open Order with the Order Id for the user");
        }

        Transaction transaction = transactionOptional.get();
        Optional<CryptoCurrency> cryptoOptional = cryptoCurrencyRepository.findBySymbolIgnoreCase(transaction.getSymbol());

        if(!cryptoOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Crypto Trading Pair is not Supported");
        }

        CryptoCurrency crypto = cryptoOptional.get();

        if(canCloseOrder(transaction.getOrderType(), closeOrderRequest.getPrice(), crypto))
        {
            transaction.setStatus(Status.CLOSE);
            transaction.setClosePrice(transaction.getOrderType().equals(OrderType.BUY) ? crypto.getAskPrice() : crypto.getBidPrice());
            transaction.setCloseDateTime(LocalDateTime.now());
            Transaction transactionResult = transactionRepository.save(transaction);
            userService.updateWallet(transaction.getUserId(), transactionResult);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Order Price");
        }
    }
    
    private boolean canPlaceOrder(OrderType orderType, BigDecimal requestPrice, CryptoCurrency crypto)
    {
        if(orderType.equals(OrderType.BUY))
        {
            return crypto.getAskPrice().compareTo(requestPrice) <= 0;
        }
        if(orderType.equals(OrderType.SELL))
        {
            return crypto.getBidPrice().compareTo(requestPrice) >= 0;
        }

        return false;
    }

    private boolean canCloseOrder(OrderType orderType, BigDecimal requestPrice, CryptoCurrency crypto)
    {
        if(orderType.equals(OrderType.BUY))
        {
            return crypto.getAskPrice().compareTo(requestPrice) >= 0;
        }
        if(orderType.equals(OrderType.SELL))
        {
            return crypto.getBidPrice().compareTo(requestPrice) <= 0;
        }

        return false;
    }
}
