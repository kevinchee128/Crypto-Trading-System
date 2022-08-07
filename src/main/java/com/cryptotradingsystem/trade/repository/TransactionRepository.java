package com.cryptotradingsystem.trade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.trade.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction,Long> {
    
    List<Transaction> findByUserIdAndStatusOrderByDateTimeDesc(Long userId, Status status);

    List<Transaction> findByUserIdAndStatusAndCurrency(Long userId, Status status, String currency);
    
    Optional<Transaction> findByIdAndStatus(Long id, Status status);

    @Modifying
    @Query(value = "UPDATE transaction SET status = 'CLOSE' WHERE user_id = ?1 AND currency = ?2", nativeQuery = true)
    void updateStatusByUserIdAndCurrency(Long userId, String currency);
}
