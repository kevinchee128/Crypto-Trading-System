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
import com.cryptotradingsystem.crypto.repository.CryptoCurrenyRepository;
import com.cryptotradingsystem.trade.dto.OrderDTO;
import com.cryptotradingsystem.trade.entity.Transaction;
import com.cryptotradingsystem.trade.repository.TransactionRepository;
import com.cryptotradingsystem.trade.request.CloseOrderRequest;
import com.cryptotradingsystem.trade.request.OrderRequest;
import com.cryptotradingsystem.trade.service.TradeService;
import com.cryptotradingsystem.user.dto.WalletDTO;
import com.cryptotradingsystem.user.entity.Wallet;
import com.cryptotradingsystem.user.repository.WalletRepository;
import com.cryptotradingsystem.user.service.UserService;

@Service
public class TradeServiceImpl implements TradeService{

    @Autowired
    CryptoCurrenyRepository cryptoCurrenyRepository;

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
        Optional<CryptoCurrency> cryptoOptional = cryptoCurrenyRepository.findBySymbolIgnoreCase(orderRequest.getSymbol());

        if(!cryptoOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Crypto Trading Pair is not Supported");
        }

        Long orderId = null;
        CryptoCurrency crypto = cryptoOptional.get();
        WalletDTO wallet = userService.calculateWalletBalance(orderRequest.getUserId(), crypto.getCurrency(), Status.OPEN);

        if(orderRequest.getOrderType().equals(OrderType.BUY)
            && crypto.getAskPrice().compareTo(orderRequest.getPrice()) <= 0
                && wallet.getBalance().compareTo(BigDecimal.ZERO) > 0)
        {
            Transaction result = transactionRepository.save(Transaction.builder()
                                            .userId(orderRequest.getUserId())
                                            .symbol(orderRequest.getSymbol())
                                            .price(orderRequest.getPrice())
                                            .orderType(orderRequest.getOrderType())
                                            .dateTime(LocalDateTime.now())
                                            .status(Status.OPEN)
                                            .amount(orderRequest.getAmount())
                                            .currency(crypto.getCurrency())
                                            .build());

            orderId = result.getId();
            return OrderDTO.builder()
                                .id(orderId)
                                .build();
        }

        if(orderRequest.getOrderType().equals(OrderType.SELL)
            && crypto.getBidPrice().compareTo(orderRequest.getPrice()) >= 0
                &&wallet.getBalance().compareTo(BigDecimal.ZERO) > 0)
        {
            Transaction result = transactionRepository.save(Transaction.builder()
                                            .userId(orderRequest.getUserId())
                                            .symbol(orderRequest.getSymbol())
                                            .price(orderRequest.getPrice())
                                            .orderType(orderRequest.getOrderType())
                                            .dateTime(LocalDateTime.now())
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
        Optional<Transaction> transactionOptional = transactionRepository.findByIdAndStatus(closeOrderRequest.getOrderId(), Status.OPEN);

        if(!transactionOptional.isPresent()) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Open Order with the Order Id");
        }

        Transaction transaction = transactionOptional.get();
        Optional<CryptoCurrency> cryptoOptional = cryptoCurrenyRepository.findBySymbolIgnoreCase(closeOrderRequest.getSymbol());

        if(!cryptoOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Crypto Trading Pair is not Supported");
        }

        CryptoCurrency crypto = cryptoOptional.get();

        Optional<Wallet> walletOptional = walletRepository.findByUserIdAndCurrency(closeOrderRequest.getUserId(), crypto.getCurrency());

        if(!walletOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Wallet with User Id and Currency");
        }

        Transaction transactionResult = null;
        Wallet wallet = walletOptional.get();

        if(closeOrderRequest.getOrderType().equals(transaction.getOrderType())
            && crypto.getAskPrice().compareTo(closeOrderRequest.getPrice()) >= 0)
        {
            transaction.setStatus(Status.CLOSE);
            transactionResult = transactionRepository.save(transaction);
        }

        if(closeOrderRequest.getOrderType().equals(transaction.getOrderType())
            && crypto.getBidPrice().compareTo(closeOrderRequest.getPrice()) <= 0)
        {
            transaction.setStatus(Status.CLOSE);
            transactionResult = transactionRepository.save(transaction);
        }

        if(transactionResult != null)
        {
            BigDecimal balance = userService.calculateTransaction(transaction, wallet.getBalance(), closeOrderRequest.getPrice());

            walletRepository.save(Wallet.builder()
                                        .id(wallet.getId())
                                        .userId(wallet.getUserId())
                                        .currency(wallet.getCurrency())
                                        .balance(balance)
                                        .build());
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order Price is too low");
        }
    }
    
}
