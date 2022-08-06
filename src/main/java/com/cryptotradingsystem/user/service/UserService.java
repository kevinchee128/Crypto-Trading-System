package com.cryptotradingsystem.user.service;

import java.util.List;

import com.cryptotradingsystem.trade.dto.TransactionDTO;
import com.cryptotradingsystem.user.dto.WalletDTO;

public interface UserService {
    
    public List<WalletDTO> getWalletBalance(Long userId);

    public List<TransactionDTO> getTradingHistory(Long userId, String status);
    
}
