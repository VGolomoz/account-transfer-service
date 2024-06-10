package com.account.transfer.service;

import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.entity.TransactionStatus;
import com.account.transfer.exception.*;
import com.account.transfer.mapper.TransactionMapper;
import com.account.transfer.repository.TransactionRepository;
import com.account.transfer.service.model.AccountModel;
import com.account.transfer.service.model.ExchangeRateModel;
import com.account.transfer.service.model.TransactionModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static com.account.transfer.entity.TransactionStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Captor
    private ArgumentCaptor<AccountModel> accountCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPerformTransfer_SameCurrency_Success() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100.0);
        ZonedDateTime dateTime = ZonedDateTime.now();

        AccountModel accountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(200.0), "USD");
        AccountModel targetAccount = prepareAccountModel(2L, targetAccountId, BigDecimal.valueOf(300.0), "USD");

        AccountModel expectedAccountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(100.0), "USD");
        AccountModel expectedTargetAccount = prepareAccountModel(2L, targetAccountId, BigDecimal.valueOf(400.0), "USD");

        TransactionEntity expectedTransactionEntity = prepareTransactionEntity(1L, accountOwner, targetAccount, amount,
                dateTime, SUCCESS, expectedAccountOwner.getBalance(), BigDecimal.ONE);
        TransactionModel expectedTransactionModel = prepareTransactionModel(1L, accountOwner, targetAccount, amount,
                dateTime, SUCCESS, expectedAccountOwner.getBalance(), BigDecimal.ONE);

        when(accountService.getAccountByOwnerId(accountOwnerId)).thenReturn(accountOwner);
        when(accountService.getAccountByOwnerId(targetAccountId)).thenReturn(targetAccount);
        doNothing().when(accountService).save(accountCaptor.capture());
        when(transactionMapper.buildTransactionEntity(accountOwner, targetAccount, amount, SUCCESS,
                expectedAccountOwner.getBalance(), BigDecimal.ONE)).thenReturn(expectedTransactionEntity);
        when(transactionRepository.save(expectedTransactionEntity)).thenReturn(expectedTransactionEntity);
        when(transactionMapper.mapToTransactionModel(expectedTransactionEntity)).thenReturn(expectedTransactionModel);

        // when
        TransactionModel result = transactionService.performTransfer(accountOwnerId, targetAccountId, amount);

        // then
        assertNotNull(result);
        assertEquals(expectedTransactionModel, result);
        assertNotNull(accountCaptor.getAllValues());
        assertEquals(2, accountCaptor.getAllValues().size());

        AccountModel capturedAccountOwner = accountCaptor.getAllValues().get(0);
        AccountModel capturedTargetAccount = accountCaptor.getAllValues().get(1);
        assertEquals(expectedAccountOwner, capturedAccountOwner);
        assertEquals(expectedTargetAccount, capturedTargetAccount);

        verify(accountService, times(2)).getAccountByOwnerId(anyLong());
        verify(accountService, times(2)).save(any(AccountModel.class));
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verify(transactionMapper, times(1)).mapToTransactionModel(any(TransactionEntity.class));
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    public void testPerformTransfer_DifferentCurrencies_Success() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal amount = BigDecimal.valueOf(100.0);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.1);
        ZonedDateTime dateTime = ZonedDateTime.now();

        AccountModel accountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(200.0), fromCurrency);
        AccountModel targetAccount = prepareAccountModel(2L, targetAccountId, BigDecimal.valueOf(300.0), toCurrency);

        AccountModel expectedAccountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(100.0), fromCurrency);
        AccountModel expectedTargetAccount = prepareAccountModel(2L, targetAccountId, BigDecimal.valueOf(410.0).setScale(2), toCurrency);

        ExchangeRateModel expectedExchangeRateModel =
                prepareExchangeRateModel(fromCurrency, toCurrency, exchangeRate, dateTime);

        TransactionEntity expectedTransactionEntity = prepareTransactionEntity(1L, accountOwner, targetAccount, amount,
                dateTime, SUCCESS, expectedAccountOwner.getBalance(), exchangeRate);
        TransactionModel expectedTransactionModel = prepareTransactionModel(1L, accountOwner, targetAccount, amount,
                dateTime, SUCCESS, expectedAccountOwner.getBalance(), exchangeRate);

        when(accountService.getAccountByOwnerId(accountOwnerId)).thenReturn(accountOwner);
        when(accountService.getAccountByOwnerId(targetAccountId)).thenReturn(targetAccount);
        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(expectedExchangeRateModel);
        doNothing().when(accountService).save(accountCaptor.capture());
        when(transactionMapper.buildTransactionEntity(accountOwner, targetAccount, amount, SUCCESS,
                expectedAccountOwner.getBalance(), exchangeRate)).thenReturn(expectedTransactionEntity);
        when(transactionRepository.save(expectedTransactionEntity)).thenReturn(expectedTransactionEntity);
        when(transactionMapper.mapToTransactionModel(expectedTransactionEntity)).thenReturn(expectedTransactionModel);

        // when
        TransactionModel result = transactionService.performTransfer(accountOwnerId, targetAccountId, amount);

        // then
        assertNotNull(result);
        assertEquals(expectedTransactionModel, result);
        assertNotNull(accountCaptor.getAllValues());
        assertEquals(2, accountCaptor.getAllValues().size());

        AccountModel capturedAccountOwner = accountCaptor.getAllValues().get(0);
        AccountModel capturedTargetAccount = accountCaptor.getAllValues().get(1);
        assertEquals(expectedAccountOwner, capturedAccountOwner);
        assertEquals(expectedTargetAccount, capturedTargetAccount);

        verify(accountService, times(2)).getAccountByOwnerId(anyLong());
        verify(exchangeRateService, times(1)).getExchangeRate(fromCurrency, toCurrency);
        verify(accountService, times(2)).save(any(AccountModel.class));
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verify(transactionMapper, times(1)).mapToTransactionModel(any(TransactionEntity.class));
    }

    @Test
    public void testPerformTransfer_Failure_when_SameAccount() {
        // given
        Long accountOwnerId = 1L;
        BigDecimal amount = BigDecimal.valueOf(100.0);
        String expectedMessage = String.format("Cannot transfer funds to the same account: %s", accountOwnerId);

        // when
        Throwable exception = Assertions.assertThrows(InvalidTransferException.class,
                () -> transactionService.performTransfer(accountOwnerId, accountOwnerId, amount));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verifyNoInteractions(accountService, exchangeRateService, transactionRepository, transactionMapper);
    }

    @Test
    public void testPerformTransfer_Failure_when_AccountOwnerIdNotFound() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100.0);

        String expectedMessage = String.format("Account with owner id: [%s] is not found", accountOwnerId);
        when(accountService.getAccountByOwnerId(accountOwnerId)).thenThrow(new AccountNotFoundException(expectedMessage));

        // when
        Throwable exception = Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.performTransfer(accountOwnerId, targetAccountId, amount));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verify(accountService, times(1)).getAccountByOwnerId(accountOwnerId);
        verifyNoInteractions(exchangeRateService, transactionRepository, transactionMapper);
    }

    @Test
    public void testPerformTransfer_Failure_when_TargetAccountIdNotFound() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100.0);

        AccountModel accountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(200.0), "USD");
        String expectedMessage = String.format("Account with owner id: [%s] is not found", targetAccountId);

        when(accountService.getAccountByOwnerId(accountOwnerId)).thenReturn(accountOwner);
        when(accountService.getAccountByOwnerId(targetAccountId)).thenThrow(new AccountNotFoundException(expectedMessage));

        // when
        Throwable exception = Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.performTransfer(accountOwnerId, targetAccountId, amount));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verify(accountService, times(2)).getAccountByOwnerId(anyLong());
        verifyNoInteractions(exchangeRateService, transactionRepository, transactionMapper);
    }

    @Test
    public void testPerformTransfer_Failure_when_InsufficientBalance() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100.0);

        AccountModel accountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(50.0), "USD");
        AccountModel targetAccount = prepareAccountModel(2L, accountOwnerId, BigDecimal.valueOf(300.0), "USD");
        String expectedMessage = String.format("Insufficient balance: %s for amount: %s", accountOwner.getBalance(), amount);

        when(accountService.getAccountByOwnerId(accountOwnerId)).thenReturn(accountOwner);
        when(accountService.getAccountByOwnerId(targetAccountId)).thenReturn(targetAccount);

        // when
        Throwable exception = Assertions.assertThrows(InsufficientBalanceException.class,
                () -> transactionService.performTransfer(accountOwnerId, targetAccountId, amount));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verify(accountService, times(2)).getAccountByOwnerId(anyLong());
        verifyNoInteractions(exchangeRateService, transactionRepository, transactionMapper);
    }

    @Test
    public void testPerformTransfer_Failure_when_ExchangeRateNotFound() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal amount = BigDecimal.valueOf(100.0);

        AccountModel accountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(200.0), fromCurrency);
        AccountModel targetAccount = prepareAccountModel(2L, targetAccountId, BigDecimal.valueOf(300.0), toCurrency);
        String expectedMessage = String.format("Exchange rate for pairs [%s:%s] is not found", fromCurrency, toCurrency);

        when(accountService.getAccountByOwnerId(accountOwnerId)).thenReturn(accountOwner);
        when(accountService.getAccountByOwnerId(targetAccountId)).thenReturn(targetAccount);
        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExchangeRateNotFoundException(fromCurrency, toCurrency));

        // when
        Throwable exception = Assertions.assertThrows(ExchangeRateNotFoundException.class,
                () -> transactionService.performTransfer(accountOwnerId, targetAccountId, amount));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verify(accountService, times(2)).getAccountByOwnerId(anyLong());
        verify(exchangeRateService, times(1)).getExchangeRate(fromCurrency, toCurrency);
        verifyNoInteractions(transactionRepository, transactionMapper);
    }

    @Test
    public void testPerformTransfer_Failure_when_ExchangeRateServiceFailed() {
        // given
        Long accountOwnerId = 1L;
        Long targetAccountId = 2L;
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal amount = BigDecimal.valueOf(100.0);

        AccountModel accountOwner = prepareAccountModel(1L, accountOwnerId, BigDecimal.valueOf(200.0), fromCurrency);
        AccountModel targetAccount = prepareAccountModel(2L, targetAccountId, BigDecimal.valueOf(300.0), toCurrency);
        String expectedMessage = String.format("Fetch latest exchange rate from=test.host for currency=%s " +
                "failed with status code=%s", fromCurrency, HttpStatus.INTERNAL_SERVER_ERROR);

        when(accountService.getAccountByOwnerId(accountOwnerId)).thenReturn(accountOwner);
        when(accountService.getAccountByOwnerId(targetAccountId)).thenReturn(targetAccount);
        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExchangeRateServiceException(expectedMessage));

        // when
        Throwable exception = Assertions.assertThrows(ExchangeRateServiceException.class,
                () -> transactionService.performTransfer(accountOwnerId, targetAccountId, amount));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verify(accountService, times(2)).getAccountByOwnerId(anyLong());
        verify(exchangeRateService, times(1)).getExchangeRate(fromCurrency, toCurrency);
        verifyNoInteractions(transactionRepository, transactionMapper);
    }

    private AccountModel prepareAccountModel(Long id, Long ownerId, BigDecimal balance, String currency) {
        return AccountModel.builder()
                .id(id)
                .ownerId(ownerId)
                .balance(balance)
                .currency(currency)
                .build();
    }

    private TransactionEntity prepareTransactionEntity(Long id, AccountModel accountOwner, AccountModel targetAccount,
                                                       BigDecimal amount, ZonedDateTime dateTime, TransactionStatus status,
                                                       BigDecimal residualBalance, BigDecimal exchangeRate) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(id);
        entity.setFromAccountId(accountOwner.getOwnerId());
        entity.setToAccountId(targetAccount.getOwnerId());
        entity.setAmount(amount);
        entity.setDateTime(dateTime);
        entity.setStatus(status);
        entity.setAvailableBalance(accountOwner.getBalance());
        entity.setResidualBalance(residualBalance);
        entity.setBaseCurrency(accountOwner.getCurrency());
        entity.setTargetCurrency(targetAccount.getCurrency());
        entity.setExchangeRate(exchangeRate);

        return entity;
    }

    private TransactionModel prepareTransactionModel(Long id, AccountModel accountOwner, AccountModel targetAccount,
                                                     BigDecimal amount, ZonedDateTime dateTime, TransactionStatus status,
                                                     BigDecimal residualBalance, BigDecimal exchangeRate) {
        return TransactionModel.builder()
                .transactionId(id)
                .accountOwnerId(accountOwner.getOwnerId())
                .targetAccountId(targetAccount.getOwnerId())
                .amount(amount)
                .dateTime(dateTime)
                .status(status)
                .availableBalance(accountOwner.getBalance())
                .residualBalance(residualBalance)
                .baseCurrency(accountOwner.getCurrency())
                .targetCurrency(targetAccount.getCurrency())
                .exchangeRate(exchangeRate)
                .build();
    }

    private ExchangeRateModel prepareExchangeRateModel(String fromCurrency, String toCurrency,
                                                       BigDecimal rate, ZonedDateTime dateTime) {
        return ExchangeRateModel.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(rate)
                .dateTime(dateTime)
                .build();
    }
}