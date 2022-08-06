package com.cryptotradingsystem.trade.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.trade.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction,Long> {
    
    List<Transaction> findByUserIdAndStatusOrderByDateTimeDesc(Long userId, String status);
}
