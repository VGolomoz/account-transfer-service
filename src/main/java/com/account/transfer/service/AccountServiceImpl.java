package com.account.transfer.service;

import com.account.transfer.exception.AccountNotFoundException;
import com.account.transfer.mapper.AccountMapper;
import com.account.transfer.repository.AccountRepository;
import com.account.transfer.service.model.AccountModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountModel getAccountByOwnerId(Long ownerId) {
        log.info("Get account data by owner id: {}", ownerId);
        return accountRepository.findByOwnerId(ownerId)
                .map(accountMapper::mapToAccountModel)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with owner id: [%s] is not found", ownerId)));
    }

    @Override
    public void save(AccountModel accountModel) {
        log.info("Save account for owner id: {}", accountModel.getOwnerId());
        accountRepository.save(accountMapper.mapToAccountEntity(accountModel));
    }
}
