package com.cryptotradingsystem.user.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<WalletDTO> getWalletBalance(Long userId) {

        List<Wallet> result = walleRepository.findByUserId(userId);
        
        return result.stream().map(w -> WalletDTO.builder()
                                            .currency(w.getCurrency())
                                            .balance(w.getBalance())
                                            .build()).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTradingHistory(Long userId, String status) {

        List<Transaction> result = transactionRepository.findByUserIdAndStatusOrderByDateTimeDesc(userId, status);

        return result.stream().map(t -> TransactionDTO.builder()
                                            .userId(t.getId())
                                            .symbol(t.getSymbol())
                                            .price(t.getPrice())
                                            .orderType(t.getOrderType())
                                            .dateTime(t.getDateTime())
                                            .status(t.getStatus())
                                            .build()).collect(Collectors.toList());
    }

    
    
}
