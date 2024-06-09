package com.account.transfer.repository;

import com.account.transfer.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link AccountEntity} entities.
 * Provides basic CRUD operations.
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Finds an account by the owner's ID.
     *
     * @param ownerId the ID of the account owner.
     * @return an Optional containing the found {@link AccountEntity}, or an empty Optional if no account was found.
     */
    Optional<AccountEntity> findByOwnerId(Long ownerId);
}
