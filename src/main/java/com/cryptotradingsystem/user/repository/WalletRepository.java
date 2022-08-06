package com.cryptotradingsystem.user.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.user.entity.Wallet;

public interface WalletRepository extends CrudRepository<Wallet, Long> {

    List<Wallet> findByUserId(Long userId);
    
}
