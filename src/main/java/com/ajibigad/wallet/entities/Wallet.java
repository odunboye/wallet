package com.ajibigad.wallet.entities;

import com.ajibigad.wallet.enums.Currency;
import com.ajibigad.wallet.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Wallet {

  @Id
  @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
  @GeneratedValue(generator = "UUIDGenerator")
  @Column(name = "id", columnDefinition = "uuid")
  private UUID id;

  @Column(length = 14, unique = true)
  private String accountNumber;

  @Column(columnDefinition = "numeric(18, 2) default 0.0 ")
  private BigDecimal balance;

  @Column(columnDefinition = "varchar(5) default 'NGN' ")
  @Enumerated(EnumType.STRING)
  private Currency currency = Currency.NGN;

  @Column(columnDefinition = "varchar(20) null default 'ACTIVE'")
  @Enumerated(EnumType.STRING)
  private WalletStatus status = WalletStatus.ACTIVE;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", updatable = false, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime created;
}
