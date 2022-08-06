package com.cryptotradingsystem.user.service.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cryptotradingsystem.common.Constants.OrderType;
import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrenyRepository;
import com.cryptotradingsystem.trade.dto.TransactionDTO;
import com.cryptotradingsystem.trade.entity.Transaction;
import com.cryptotradingsystem.trade.repository.TransactionRepository;
import com.cryptotradingsystem.user.dto.WalletDTO;
import com.cryptotradingsystem.user.entity.Wallet;
import com.cryptotradingsystem.user.repository.WalletRepository;
import com.cryptotradingsystem.user.service.UserService;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    WalletRepository walleRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired 
    CryptoCurrenyRepository cryptoCurrenyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WalletDTO> getWalletBalance(Long userId) 
    {
        List<Wallet> result = walleRepository.findByUserId(userId);

        if(result.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have any Wallet");
        }
        
        return result.stream().map(w -> WalletDTO.builder()
                                            .id(w.getId())
                                            .currency(w.getCurrency())
                                            .balance(w.getBalance())
                                            .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTradingHistory(Long userId, Status status) 
    {

        List<Transaction> result = transactionRepository.findByUserIdAndStatusOrderByDateTimeDesc(userId, status);

        if(result.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have any Trading History with Status: " + status);
        }

        return result.stream().map(t -> TransactionDTO.builder()
                                            .userId(t.getId())
                                            .symbol(t.getSymbol())
                                            .price(t.getPrice())
                                            .orderType(t.getOrderType())
                                            .dateTime(t.getDateTime())
                                            .status(t.getStatus())
                                            .amount(t.getAmount())
                                            .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WalletDTO calculateWalletBalance(Long userId, String currency, Status status) 
    {

        Optional<Wallet> walletOptional = walleRepository.findByUserIdAndCurrency(userId, currency);

        if(!walletOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Wallet with User Id and Currency");
        }

        Wallet wallet = walletOptional.get();
        BigDecimal balance = wallet.getBalance();

        List<Transaction> transactionList = transactionRepository.findByUserIdAndStatusAndCurrency(userId, status, currency);

        for(int i = 0; i < transactionList.size(); i++)
        {
            calculateTransaction(transactionList.get(i), balance);
        }
        
        return WalletDTO.builder()
                    .id(wallet.getId())
                    .balance(balance)
                    .currency(currency)
                    .build();

    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTransaction(Transaction transaction, BigDecimal balance)
    {
        CryptoCurrency cryptoCurrency = null;

        Optional<CryptoCurrency> cryptoCurrencyOptional = cryptoCurrenyRepository.findBySymbolIgnoreCase(transaction.getSymbol());
        if (cryptoCurrencyOptional.isPresent()) 
        {
            cryptoCurrency = cryptoCurrencyOptional.get();
            if(transaction.getOrderType().equals(OrderType.BUY))
            {
                BigDecimal difference = transaction.getPrice().subtract(cryptoCurrency.getAskPrice());
                BigDecimal profitLoss = difference.multiply(transaction.getAmount());
                balance = balance.subtract(profitLoss);
            }
            if(transaction.getOrderType().equals(OrderType.SELL))
            {
                BigDecimal difference = transaction.getPrice().subtract(cryptoCurrency.getBidPrice());
                BigDecimal profitLoss = difference.multiply(transaction.getAmount());
                balance = balance.add(profitLoss);
            }
        }

        return balance;
    }
}
