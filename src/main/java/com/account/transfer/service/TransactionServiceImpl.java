package com.account.transfer.service;

import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.exception.InsufficientBalanceException;
import com.account.transfer.exception.InvalidTransferException;
import com.account.transfer.mapper.TransactionMapper;
import com.account.transfer.repository.TransactionRepository;
import com.account.transfer.service.model.AccountModel;
import com.account.transfer.service.model.TransactionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.account.transfer.entity.TransactionStatus.SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;
    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionModel performTransfer(Long accountOwnerId, Long targetAccountId, BigDecimal amount) {
        log.info("Perform transfer amount: {}, from account: {}, to account: {}", amount, accountOwnerId, targetAccountId);
        if (accountOwnerId.equals(targetAccountId)) {
            throw new InvalidTransferException("Cannot transfer funds to the same account: " + accountOwnerId);
        }

        var accountOwner = accountService.getAccountByOwnerId(accountOwnerId);
        var targetAccount = accountService.getAccountByOwnerId(targetAccountId);

        validateBalance(accountOwner, amount);

        var transaction = accountOwner.getCurrency().equals(targetAccount.getCurrency())
                ? performSameCurrenciesTransfer(accountOwner, targetAccount, amount)
                : performDifferentCurrenciesTransfer(accountOwner, targetAccount, amount);

        transaction = transactionRepository.save(transaction);
        log.info("Transaction with id: {} saved with status: {}", transaction.getId(), transaction.getStatus());
        return transactionMapper.mapToTransactionModel(transaction);
    }

    private void validateBalance(AccountModel accountOwner, BigDecimal amount) {
        var currentBalance = accountOwner.getBalance();
        var isBalanceInsufficient = currentBalance.compareTo(amount) < 0;

        if (isBalanceInsufficient) {
            throw new InsufficientBalanceException("Insufficient balance: " + currentBalance + " for amount: " + amount);
        }
    }

    private TransactionEntity performSameCurrenciesTransfer(AccountModel accountOwner, AccountModel targetAccount,
                                                            BigDecimal amount) {
        log.info("Perform the same currencies transfer, from currency: {}, to currency: {}",
                accountOwner.getCurrency(), targetAccount.getCurrency());
        var updatedAccountOwner = accountOwner.toBuilder()
                .balance(accountOwner.getBalance().subtract(amount))
                .build();
        accountService.save(updatedAccountOwner);

        var updatedTargetAccount = targetAccount.toBuilder()
                .balance(targetAccount.getBalance().add(amount))
                .build();
        accountService.save(updatedTargetAccount);

        return transactionMapper.buildTransactionEntity(accountOwner, targetAccount, amount, SUCCESS,
                updatedAccountOwner.getBalance(), BigDecimal.ONE);
    }

    private TransactionEntity performDifferentCurrenciesTransfer(AccountModel accountOwner, AccountModel targetAccount,
                                                                 BigDecimal amount) {
        var fromCurrency = accountOwner.getCurrency();
        var toCurrency = targetAccount.getCurrency();
        log.info("Perform the different currencies transfer, from currency: {}, to currency: {}", fromCurrency, toCurrency);
        var actualExchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
        var exchangeAmount = amount.multiply(actualExchangeRate.getRate()).setScale(2, RoundingMode.HALF_UP);

        var updatedAccountOwner = accountOwner.toBuilder()
                .balance(accountOwner.getBalance().subtract(amount))
                .build();
        accountService.save(updatedAccountOwner);

        var updatedTargetAccount = targetAccount.toBuilder()
                .balance(targetAccount.getBalance().add(exchangeAmount))
                .build();
        accountService.save(updatedTargetAccount);

        return transactionMapper.buildTransactionEntity(accountOwner, targetAccount, amount, SUCCESS,
                updatedAccountOwner.getBalance(), actualExchangeRate.getRate());
    }
}
