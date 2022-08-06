package com.cryptotradingsystem.crypto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cryptotradingsystem.crypto.service.SchedulerService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CryptoScheduler {
    
    @Autowired
    SchedulerService schedulerService;

    @Scheduled(fixedRate = 10000)
    public void getCryptoSchedulerPrice()
    {
        log.info("Retrieve Latest Price");
        schedulerService.getLatestPrice();
    }
}
