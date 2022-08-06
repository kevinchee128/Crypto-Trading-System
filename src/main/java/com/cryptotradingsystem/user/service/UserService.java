package com.cryptotradingsystem.user.service;

import java.math.BigDecimal;
import java.util.List;

import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.trade.dto.TransactionDTO;
import com.cryptotradingsystem.trade.entity.Transaction;
import com.cryptotradingsystem.user.dto.WalletDTO;

public interface UserService {
    
    public List<WalletDTO> getWalletBalance(Long userId);

    public List<TransactionDTO> getTradingHistory(Long userId, Status status);

    public WalletDTO calculateWalletBalance(Long userId, String currency, Status status);

    public BigDecimal calculateTransaction(Transaction transaction, BigDecimal balance);
    
}
