package com.cryptotradingsystem.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.cryptotradingsystem.user.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    
}
