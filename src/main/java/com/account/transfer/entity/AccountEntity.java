package com.account.transfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class AccountEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

}
