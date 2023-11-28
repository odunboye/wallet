package com.ajibigad.wallet.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payonus.walletlib.enums.Currency;
import com.payonus.walletlib.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class Wallet {

  @Id
  @UuidGenerator
  @Column(name = "wallet_id", columnDefinition = "uuid")
  private UUID id;

  @NotBlank
  @Column(name = "account_number", length = 14, unique = true)
  private String accountNumber;

  @Column(columnDefinition = "numeric(18, 2) default 0.0 ")
  private BigDecimal balance;

  @Builder.Default
  @Column(columnDefinition = "varchar(5) default 'NGN' ")
  @Enumerated(EnumType.STRING)
  private Currency currency = Currency.NGN;

  @Builder.Default
  @Column(columnDefinition = "varchar(20) null default 'ACTIVE'")
  @Enumerated(EnumType.STRING)
  private WalletStatus status = WalletStatus.ACTIVE;

  @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL, CascadeType.REMOVE })
  @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
  private Customer customer;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime created;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = true, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
  private Date updated;

  @JsonIgnore
  @Builder.Default
  @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<Lien> liens = new HashSet<>();

}
