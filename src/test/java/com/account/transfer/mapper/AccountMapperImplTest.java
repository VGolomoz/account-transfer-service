package com.account.transfer.mapper;

import com.account.transfer.entity.AccountEntity;
import com.account.transfer.service.model.AccountModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountMapperImplTest {

    private final AccountMapperImpl accountMapper = new AccountMapperImpl();

    @Test
    public void testMapToAccountModel_ValidEntity() {
        // given
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setOwnerId(123L);
        accountEntity.setCurrency("USD");
        accountEntity.setBalance(BigDecimal.valueOf(1000.0));

        // when
        AccountModel accountModel = accountMapper.mapToAccountModel(accountEntity);

        // then
        assertNotNull(accountModel);

        assertEquals(accountEntity.getId(), accountModel.getId());
        assertEquals(accountEntity.getOwnerId(), accountModel.getOwnerId());
        assertEquals(accountEntity.getCurrency(), accountModel.getCurrency());
        assertEquals(accountEntity.getBalance(), accountModel.getBalance());
    }

    @Test
    public void testMapToAccountModel_NullEntity() {
        // when
        AccountModel accountModel = accountMapper.mapToAccountModel(null);

        // then
        assertNull(accountModel);
    }

    @Test
    public void testMapToAccountEntity_ValidModel() {
        // given
        AccountModel accountModel = AccountModel.builder()
                .id(1L)
                .ownerId(123L)
                .currency("USD")
                .balance(BigDecimal.valueOf(1000.0))
                .build();

        // when
        AccountEntity accountEntity = accountMapper.mapToAccountEntity(accountModel);

        // then
        assertNotNull(accountEntity);

        assertEquals(accountModel.getId(), accountEntity.getId());
        assertEquals(accountModel.getOwnerId(), accountEntity.getOwnerId());
        assertEquals(accountModel.getCurrency(), accountEntity.getCurrency());
        assertEquals(accountModel.getBalance(), accountEntity.getBalance());
    }

    @Test
    public void testMapToAccountEntity_NullModel() {
        // when
        AccountEntity accountEntity = accountMapper.mapToAccountEntity(null);

        // then
        assertNull(accountEntity);
    }
}