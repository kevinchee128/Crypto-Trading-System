package com.cryptotradingsystem.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotradingsystem.common.Constants;
import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.trade.dto.TransactionDTO;
import com.cryptotradingsystem.user.dto.WalletDTO;
import com.cryptotradingsystem.user.service.UserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(Constants.V1 + "/user")
public class UserController {

    @Autowired 
    UserService userService;
    
    @GetMapping("/balance")
    public ResponseEntity<List<WalletDTO>> getWalletBalance(@RequestParam(required = true) Long userId) 
    {
        log.info("API Called: getWalletBalance");
        List<WalletDTO> result = userService.getWalletBalance(userId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionDTO>> getTradingHistory(@RequestParam(required = true) Long userId, @RequestParam(required = true) Status status) 
    {
        log.info("API Called: getTradingHistory");
        List<TransactionDTO> result = userService.getTradingHistory(userId, status);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
