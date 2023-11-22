package com.ajibigad.wallet.entities;

import com.ajibigad.wallet.enums.Currency;
import com.ajibigad.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
public class WalletTransaction {

  @Id
  @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
  @GeneratedValue(generator = "UUIDGenerator")
  @Column(name = "id", columnDefinition = "uuid")
  private UUID id;

  @Column(length = 14)
  private String accountNumber;

  @Column(columnDefinition = "numeric(18, 2) default 0.0 ", precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(columnDefinition = "numeric(18, 2) default 0.0 ", precision = 10, scale = 2)
  private BigDecimal signedAmount;

  @Column(columnDefinition = "varchar(5) default 'NGN' ")
  @Enumerated(EnumType.STRING)
  private Currency currency = Currency.NGN;

  @Column(columnDefinition = "numeric(18, 2) default 0.0 ", precision = 10, scale = 2)
  private BigDecimal balanceBefore;

  @Column(columnDefinition = "numeric(18, 2) default 0.0 ", precision = 10, scale = 2)
  private BigDecimal balanceAfter;

  @Column(columnDefinition = "varchar(20) not null")
  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  @Column(length = 155, unique = true)
  private String reference;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(
      name = "created",
      updatable = false,
      columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime created;

  @ManyToOne
  private Wallet wallet;
}
