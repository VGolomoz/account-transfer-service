package com.account.transfer.service;

import com.account.transfer.entity.AccountEntity;
import com.account.transfer.exception.AccountNotFoundException;
import com.account.transfer.mapper.AccountMapper;
import com.account.transfer.repository.AccountRepository;
import com.account.transfer.service.model.AccountModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAccountByOwnerId_ExistingAccount() {
        // given
        Long ownerId = 123L;
        AccountEntity accountEntity = prepareAccountEntity(1L, ownerId, BigDecimal.valueOf(1000.0), "USD");
        AccountModel expectedAccountModel = prepareAccountModel(1L, ownerId, BigDecimal.valueOf(1000.0), "USD");

        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(accountEntity));
        when(accountMapper.mapToAccountModel(accountEntity)).thenReturn(expectedAccountModel);

        // when
        AccountModel actualAccountModel = accountService.getAccountByOwnerId(ownerId);

        // then
        assertNotNull(actualAccountModel);
        assertEquals(expectedAccountModel, actualAccountModel);
    }

    @Test
    public void testGetAccountByOwnerId_NonExistingAccount() {
        // given
        Long ownerId = 123L;
        String expectedMessage = String.format("Account with owner id: [%s] is not found", ownerId);
        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        // when
        Throwable exception = Assertions.assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountByOwnerId(ownerId));

        // then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testSave() {
        // given
        AccountModel accountModel = prepareAccountModel(1L, 123L, BigDecimal.valueOf(1000.0), "USD");
        AccountEntity accountEntity = prepareAccountEntity(1L, 123L, BigDecimal.valueOf(1000.0), "USD");

        when(accountMapper.mapToAccountEntity(accountModel)).thenReturn(accountEntity);

        // when
        accountService.save(accountModel);

        // then
        verify(accountRepository).save(accountEntity);
    }

    private AccountEntity prepareAccountEntity(Long id, Long ownerId, BigDecimal balance, String currency) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setOwnerId(ownerId);
        accountEntity.setBalance(balance);
        accountEntity.setCurrency(currency);

        return accountEntity;
    }

    private AccountModel prepareAccountModel(Long id, Long ownerId, BigDecimal balance, String currency) {
        return AccountModel.builder()
                .id(id)
                .ownerId(ownerId)
                .balance(balance)
                .currency(currency)
                .build();
    }

}