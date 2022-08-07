package com.cryptotradingsystem.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptotradingsystem.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
