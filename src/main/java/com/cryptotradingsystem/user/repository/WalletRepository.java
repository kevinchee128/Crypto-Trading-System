package com.cryptotradingsystem.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.user.entity.Wallet;

public interface WalletRepository extends CrudRepository<Wallet, Long> {

    List<Wallet> findByUserId(Long userId);

    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);

    @Modifying
    @Query(value = "UPDATE wallet SET balance = 0 WHERE user_id = ?1 AND currency = ?2", nativeQuery = true)
    void updateBalanceByUserIdAndCurrency( Long userId, String currency);
}
