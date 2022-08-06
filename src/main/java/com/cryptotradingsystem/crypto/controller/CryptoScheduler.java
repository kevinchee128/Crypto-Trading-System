package com.cryptotradingsystem.crypto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cryptotradingsystem.crypto.service.SchedulerService;

@Component
public class CryptoScheduler {
    
    @Autowired
    SchedulerService schedulerService;

    @Scheduled(fixedRate = 10000)
    public void getCryptoSchedulerPrice()
    {
        schedulerService.getLatestPrice();
    }
}
