package com.cryptotradingsystem.crypto.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.cryptotradingsystem.common.Constants.OrderType;
import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrencyRepository;
import com.cryptotradingsystem.crypto.response.BinanceResponse;
import com.cryptotradingsystem.crypto.response.HoubiResponse;
import com.cryptotradingsystem.crypto.response.HoubiResponseList;
import com.cryptotradingsystem.crypto.service.SchedulerService;
import com.cryptotradingsystem.trade.entity.Transaction;
import com.cryptotradingsystem.trade.repository.TransactionRepository;
import com.cryptotradingsystem.user.entity.User;
import com.cryptotradingsystem.user.entity.Wallet;
import com.cryptotradingsystem.user.repository.UserRepository;
import com.cryptotradingsystem.user.repository.WalletRepository;
import com.cryptotradingsystem.user.service.UserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SchedulerServiceImpl implements SchedulerService{

	@Autowired
	CryptoCurrencyRepository cryptoCurrenyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	UserService userService;

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

			checkAccountBalance();
		} 
		catch(Exception e) 
		{
			log.error("Error occured", e);
		}

	}

	private void checkAccountBalance()
	{
		LocalDateTime timeNow = LocalDateTime.now();
		List<User> userList = userRepository.findAll();
		List<CryptoCurrency> cryptoList = cryptoCurrenyRepository.findAll();
		Map<String, CryptoCurrency> cryptoMap = cryptoList.stream().collect(Collectors.toMap(CryptoCurrency::getSymbol, Function.identity()));

		for(User user : userList)
		{
			List<Wallet> walletList = walletRepository.findByUserId(user.getId());

			for(Wallet wallet : walletList)
			{
				BigDecimal balance = userService.calculateWalletBalance(user.getId(), wallet.getCurrency());

				if(balance.compareTo(BigDecimal.ZERO) < 0 )
				{
					log.info("Force Closing Open Transactions");
					List<Transaction> transactionList = transactionRepository.findByUserIdAndStatusAndCurrency(user.getId(), Status.OPEN, wallet.getCurrency());

					for(Transaction transaction : transactionList)
					{
						BigDecimal closePrice = transaction.getOrderType().equals(OrderType.BUY) ? cryptoMap.get(transaction.getSymbol()).getAskPrice() : cryptoMap.get(transaction.getSymbol()).getBidPrice();

						transaction.setStatus(Status.CLOSE);
						transaction.setCloseDateTime(timeNow);
						transaction.setClosePrice(closePrice);
						transactionRepository.save(transaction);
					}
					
					walletRepository.updateBalanceByUserIdAndCurrency(user.getId(), wallet.getCurrency());
				}
			}
		}
	}

	private void comparePrice(BinanceResponse binanceCrpytoData, HoubiResponse houbiCrpytoData)
	{
		BigDecimal askPrice = (binanceCrpytoData.getAskPrice().compareTo(houbiCrpytoData.getAsk()) < 0) ? binanceCrpytoData.getAskPrice() : houbiCrpytoData.getAsk();
		BigDecimal bidPrice = (binanceCrpytoData.getBidPrice().compareTo(houbiCrpytoData.getBid()) > 0) ? binanceCrpytoData.getBidPrice() : houbiCrpytoData.getBid();

		CryptoCurrency crypto = CryptoCurrency.builder()
			.symbol(binanceCrpytoData.getSymbol())
			.askPrice(askPrice)
			.bidPrice(bidPrice)
			.currency(getCurrencyBySymbol(binanceCrpytoData.getSymbol()))
			.build();

		storePrice(crypto);
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

