package com.account.transfer.repository;

import com.account.transfer.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link TransactionEntity} entities.
 * Provides basic CRUD operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
