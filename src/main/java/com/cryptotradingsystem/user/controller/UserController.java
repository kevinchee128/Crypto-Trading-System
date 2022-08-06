package com.cryptotradingsystem.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotradingsystem.trade.dto.TransactionDTO;
import com.cryptotradingsystem.user.dto.WalletDTO;
import com.cryptotradingsystem.user.service.UserService;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired 
    UserService userService;
    
    @GetMapping("/balance")
    public ResponseEntity<Object> getWalletBalance(@RequestParam(required = true) Long userId){
        
        List<WalletDTO> result = userService.getWalletBalance(userId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getTradingHistory(@RequestParam(required = true) Long userId, @RequestParam(required = true) String status) {

        List<TransactionDTO> result = userService.getTradingHistory(userId, status);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
