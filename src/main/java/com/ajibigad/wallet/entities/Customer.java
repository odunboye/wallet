package com.ajibigad.wallet.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class Customer {

  @Id
  @Column(name = "customer_id", updatable = false, nullable = false)
  @UuidGenerator
  private UUID id;

  @NotEmpty
  @JsonProperty("first_name")
  private String firstName;

  @NotEmpty
  @JsonProperty("last_name")
  private String lastName;

  @NotEmpty
  private String email;

  @NotEmpty
  private String phoneNumber;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false)
  private LocalDateTime created;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated")
  private LocalDateTime updated;

  @JsonIgnore
  @Builder.Default
  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<Wallet> wallets = new HashSet<>();

}