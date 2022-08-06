package com.cryptotradingsystem.crypto.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrenyRepository;
import com.cryptotradingsystem.crypto.response.BinanceResponse;
import com.cryptotradingsystem.crypto.response.HoubiResponse;
import com.cryptotradingsystem.crypto.response.HoubiResponseList;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SchedulerService {

	@Autowired
	private CryptoCurrenyRepository cryptoCurrenyRepository;

    @Value("${server.binance.url}")
	private String binanceUrl;

	@Value("${server.houbi.url}")
	private String houbiUrl;

	@Value("${supported.crypto}")
	private String[] supportedCrypto;
    
	@Transactional
	public void getLatestPrice()
	{
		try
		{
			RestTemplate restTemplate = new RestTemplate();

			BinanceResponse[] binanceResult = restTemplate.getForObject(binanceUrl, BinanceResponse[].class);
			HoubiResponseList houbiResult = restTemplate.getForObject(houbiUrl, HoubiResponseList.class);
			List<HoubiResponse> houbiList = houbiResult.getData();

			List<String> supportedSymbols = Arrays.asList(supportedCrypto);
			List<BinanceResponse> binanceList = Arrays.asList(binanceResult);

			supportedSymbols.forEach(supportedSymbol -> 
			{
				BinanceResponse binanceSymbol = null;
				HoubiResponse huobiSymbol = null;
	
				Optional<BinanceResponse> binanceSymbolOptional = binanceList.stream().filter(b -> b.getSymbol().equalsIgnoreCase(supportedSymbol)).findFirst();
				if (binanceSymbolOptional.isPresent()) 
				{
					binanceSymbol = binanceSymbolOptional.get();
				}

				Optional<HoubiResponse> huobiSymbolOptional = houbiList.stream().filter(h -> h.getSymbol().equalsIgnoreCase(supportedSymbol)).findFirst();
				if (huobiSymbolOptional.isPresent()) 
				{
					huobiSymbol = huobiSymbolOptional.get();
				}

				if (binanceSymbol != null && huobiSymbol != null) 
				{
					comparePrice(binanceSymbol, huobiSymbol);
				}
			});

		} catch(Exception e) {
			log.error("Error occured", e);
		}

	}

	private void comparePrice(BinanceResponse binanceCrpytoData, HoubiResponse houbiCrpytoData)
	{
		BigDecimal askPrice = (binanceCrpytoData.getAskPrice().compareTo(houbiCrpytoData.getAsk()) < 0) ? binanceCrpytoData.getAskPrice() : houbiCrpytoData.getAsk();
		BigDecimal bidPrice = (binanceCrpytoData.getBidPrice().compareTo(houbiCrpytoData.getBid()) > 0) ? binanceCrpytoData.getBidPrice() : houbiCrpytoData.getBid();

		CryptoCurrency currency = CryptoCurrency.builder()
			.symbol(binanceCrpytoData.getSymbol())
			.askPrice(askPrice)
			.bidPrice(bidPrice)
			.currency(getCurrencyBySymbol(binanceCrpytoData.getSymbol()))
			.build();

		storePrice(currency);
	}

	private void storePrice(CryptoCurrency cryptoCurrency)
	{
		Optional<CryptoCurrency> cryptoCurrencyDB = cryptoCurrenyRepository.findBySymbolIgnoreCase(cryptoCurrency.getSymbol());
		if(cryptoCurrencyDB.isPresent())
		{
			CryptoCurrency cryptoCurrencyToSave = cryptoCurrencyDB.get();
			cryptoCurrencyToSave.setAskPrice(cryptoCurrency.getAskPrice());
			cryptoCurrencyToSave.setBidPrice(cryptoCurrency.getBidPrice());
			cryptoCurrenyRepository.save(cryptoCurrencyToSave);
		}
		else
		{
			cryptoCurrenyRepository.save(cryptoCurrency);
		}
	}

	private String getCurrencyBySymbol(String symbol)
	{
		String value = "";

		if(symbol.equalsIgnoreCase("ETHUSDT") || symbol.equalsIgnoreCase("BTCUSDT"))
		{
			value = "USDT";
		}

		return value;
	}
}

