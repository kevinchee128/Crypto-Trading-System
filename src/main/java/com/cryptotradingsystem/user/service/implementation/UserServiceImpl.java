package com.cryptotradingsystem.user.service.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cryptotradingsystem.common.Constants.OrderType;
import com.cryptotradingsystem.common.Constants.Status;
import com.cryptotradingsystem.crypto.entity.CryptoCurrency;
import com.cryptotradingsystem.crypto.repository.CryptoCurrencyRepository;
import com.cryptotradingsystem.trade.dto.TransactionDTO;
import com.cryptotradingsystem.trade.entity.Transaction;
import com.cryptotradingsystem.trade.repository.TransactionRepository;
import com.cryptotradingsystem.user.dto.WalletDTO;
import com.cryptotradingsystem.user.entity.User;
import com.cryptotradingsystem.user.entity.Wallet;
import com.cryptotradingsystem.user.repository.UserRepository;
import com.cryptotradingsystem.user.repository.WalletRepository;
import com.cryptotradingsystem.user.service.UserService;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired 
    CryptoCurrencyRepository cryptoCurrencyRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WalletDTO> getWalletBalance(Long userId) 
    {
        List<Wallet> result = walletRepository.findByUserId(userId);

        if(result.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have any Wallet");
        }
        
        return result.stream().map(w -> WalletDTO.builder()
                                            .id(w.getId())
                                            .currency(w.getCurrency())
                                            .balance(w.getBalance())
                                            .currentBalance(calculateWalletBalance(w.getUserId(), w.getCurrency()))
                                            .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTradingHistory(Long userId, Status status) 
    {
        Optional<User> user = userRepository.findById(userId);

        if(!user.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not Found");
        }

        List<Transaction> result = transactionRepository.findByUserIdAndStatusOrderByOpenDateTimeDesc(userId, status);

        return result.stream().map(t -> TransactionDTO.builder()
                                            .userId(t.getId())
                                            .symbol(t.getSymbol())
                                            .openPrice(t.getOpenPrice())
                                            .closePrice(t.getClosePrice())
                                            .orderType(t.getOrderType())
                                            .openDateTime(t.getOpenDateTime())
                                            .closeDateTime(t.getCloseDateTime())
                                            .status(t.getStatus())
                                            .amount(t.getAmount())
                                            .orderId(t.getId())
                                            .build()).collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateWalletBalance(Long userId, String currency) 
    {
        Optional<Wallet> walletOptional = walletRepository.findByUserIdAndCurrency(userId, currency);

        if(!walletOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Wallet with User Id and Currency");
        }

        Wallet wallet = walletOptional.get();
        BigDecimal balance = wallet.getBalance();

        List<Transaction> transactionList = transactionRepository.findByUserIdAndStatusAndCurrency(userId, Status.OPEN, currency);
        List<CryptoCurrency> cryptoCurrencyList = cryptoCurrencyRepository.findAll();
        Map<String, CryptoCurrency> cryptoCurrencyMap = cryptoCurrencyList.stream().collect(Collectors.toMap(CryptoCurrency::getSymbol, Function.identity()));

        for(Transaction transaction: transactionList)
        {
            if(!cryptoCurrencyMap.containsKey(transaction.getSymbol()))
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find Symbol");
            }

            CryptoCurrency cryptoCurrency = cryptoCurrencyMap.get(transaction.getSymbol());

            if(transaction.getOrderType().equals(OrderType.BUY))
            {
                BigDecimal profitLoss = (cryptoCurrency.getAskPrice().subtract(transaction.getOpenPrice())).multiply(transaction.getAmount());
                balance = balance.add(profitLoss);
            }
            if(transaction.getOrderType().equals(OrderType.SELL))
            {
                BigDecimal profitLoss = (transaction.getOpenPrice().subtract(cryptoCurrency.getBidPrice())).multiply(transaction.getAmount());
                balance = balance.add(profitLoss);
            }
        }

        return balance;
    }

    @Override
    public void updateWallet(Long userId, Transaction transaction)
    {
        Optional<Wallet> walletOptional = walletRepository.findByUserIdAndCurrency(userId, transaction.getCurrency());

        if(!walletOptional.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Wallet with User Id and Currency");
        }

        Wallet wallet = walletOptional.get();

        BigDecimal balance = calculateTransaction(transaction, wallet.getBalance());
        wallet.setBalance(balance);
        walletRepository.save(wallet);
    }

    private BigDecimal calculateTransaction(Transaction transaction, BigDecimal balance)
    {
        if(transaction.getOrderType().equals(OrderType.BUY))
        {
            BigDecimal difference = transaction.getOpenPrice().subtract(transaction.getClosePrice());
            BigDecimal profitLoss = difference.multiply(transaction.getAmount());
            balance = balance.subtract(profitLoss);
        }
        if(transaction.getOrderType().equals(OrderType.SELL))
        {
            BigDecimal difference = transaction.getOpenPrice().subtract(transaction.getClosePrice());
            BigDecimal profitLoss = difference.multiply(transaction.getAmount());
            balance = balance.add(profitLoss);
        }

        return balance;
    }
}
