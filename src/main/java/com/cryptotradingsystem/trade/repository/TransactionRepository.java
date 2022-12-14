package com.cryptotradingsystem.trade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.trade.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction,Long> {
    
    List<Transaction> findByUserIdAndStatusOrderByOpenDateTimeDesc(Long userId, Status status);

    List<Transaction> findByUserIdAndStatusAndCurrency(Long userId, Status status, String currency);
    
    Optional<Transaction> findByIdAndUserIdAndStatus(Long id, Long userId, Status status);
}
