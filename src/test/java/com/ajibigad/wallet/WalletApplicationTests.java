// package com.ajibigad.wallet;

// import com.ajibigad.wallet.entities.Wallet;
// import com.ajibigad.wallet.entities.WalletTransaction;
// import com.ajibigad.wallet.enums.Currency;
// import com.ajibigad.wallet.enums.TransactionType;
// import com.ajibigad.wallet.repository.WalletRepository;
// import com.ajibigad.wallet.repository.WalletTransactionRepository;
// import com.ajibigad.wallet.services.WalletService;
// import com.ajibigad.wallet.utils.BigDecimalUtils;
// import org.assertj.core.api.Assertions;
// import org.junit.ClassRule;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.util.TestPropertyValues;
// import org.springframework.context.ApplicationContextInitializer;
// import org.springframework.context.ConfigurableApplicationContext;
// import org.springframework.test.context.ContextConfiguration;

// import org.springframework.test.context.junit4.SpringRunner;
// import org.testcontainers.containers.PostgreSQLContainer;

// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;
// import java.util.UUID;
// import java.util.concurrent.*;

// @RunWith(SpringRunner.class)
// @SpringBootTest
// @ContextConfiguration(initializers = {
// WalletApplicationTests.Initializer.class })
// public class WalletApplicationTests {

// private final String MERCHANT_ACCT = "0001";
// private final String PROVIDER_ACCT = "0002";
// private final String PAYONUS_ACCT = "0003";

// @ClassRule
// public static PostgreSQLContainer<?> postgres = new
// PostgreSQLContainer<>("postgres")
// .withDatabaseName("postgres")
// .withUsername("postgres")
// .withPassword("docker")
// .withInitScript("test-wallet-schema.sql");

// @Autowired
// WalletService walletService;

// @Autowired
// WalletRepository walletRepository;

// @Autowired
// WalletTransactionRepository walletTransactionRepository;

// @Test
// public void contextLoads() {
// }

// @Test
// public void testWallet() {
// // Create thread pool with 5 threads
// // Simulate several request to an account with random amounts and sometimes
// // credit, sometimes debit
// // Fetch the wallet balance from wallet table
// // Fetch sum of all transactions for the test account and assert the result
// is
// // equal to the balance in the wallet

// // Each request
// // Credit -> 3 transactions
// // 0.75 -> 0001 (Merchant), 0.05 -> 0002 (Provider), 0.20 -> 0003 (Payonus)
// // Debit -> 2 transactions
// // DB -> 0001, CR -> 0003
// Random random = new Random(System.currentTimeMillis());
// List<Callable<Void>> tasks = new ArrayList<>();
// long[] totalAmounts = new long[1];

// ExecutorService executorService = Executors.newFixedThreadPool(10);
// random.longs(200, 1, 10000)
// .forEach(l -> {
// totalAmounts[0] += l;
// BigDecimal amount = BigDecimal.valueOf(l, 2);
// tasks.add(() -> {
// walletService.processTransactions(generateTransactionsForCredit(amount));
// return null;
// });
// tasks.add(() -> {
// walletService.processTransactions(
// generateTransactionsForDebit(BigDecimalUtils.multiply(0.02, amount)));
// return null;
// });
// });
// try {
// List<Future<Void>> futures = executorService.invokeAll(tasks);
// long failureCount = futures.stream().filter(f -> {
// try {
// f.get();
// return false;
// } catch (CancellationException | ExecutionException e) {
// e.printStackTrace();
// return true;
// } catch (InterruptedException e) {
// throw new RuntimeException(e);
// }
// }).count();
// System.err.println("Number of failed executions " + failureCount);
// Assertions.assertThat(failureCount).isZero();
// } catch (InterruptedException e) {
// throw new RuntimeException(e);
// }

// executorService.shutdown();
// try {
// if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
// executorService.shutdownNow();
// }
// } catch (InterruptedException e) {
// executorService.shutdownNow();
// }

// System.out.println("Total transactions created -> " +
// walletTransactionRepository.count());

// Wallet merchantWallet = walletRepository.findByAccountNumber(MERCHANT_ACCT);
// Wallet providerWallet = walletRepository.findByAccountNumber(PROVIDER_ACCT);
// Wallet payonusWallet = walletRepository.findByAccountNumber(PAYONUS_ACCT);

// BigDecimal merchantBalance =
// walletTransactionRepository.getRolledUpBalance(MERCHANT_ACCT);
// BigDecimal providerBalance =
// walletTransactionRepository.getRolledUpBalance(PROVIDER_ACCT);
// BigDecimal payonusBalance =
// walletTransactionRepository.getRolledUpBalance(PAYONUS_ACCT);

// Assertions.assertThat(merchantWallet.getBalance()).isEqualTo(merchantBalance);
// Assertions.assertThat(providerWallet.getBalance()).isEqualTo(providerBalance);
// Assertions.assertThat(payonusWallet.getBalance()).isEqualTo(payonusBalance);

// System.out.println(merchantWallet.getBalance().toPlainString() + " -> " +
// merchantBalance.toPlainString());
// System.out.println(providerWallet.getBalance().toPlainString() + " -> " +
// providerBalance.toPlainString());
// System.out.println(payonusWallet.getBalance().toPlainString() + " -> " +
// payonusBalance.toPlainString());

// // View loss or gain due to rounding
// BigDecimal totalAmount = BigDecimal.valueOf(totalAmounts[0], 2);

// BigDecimal merchantBalanceWithHigherPrecision =
// BigDecimalUtils.multiply(0.75, totalAmount, false)
// .subtract(BigDecimalUtils.multiply(0.02, totalAmount, false));
// BigDecimal providerBalanceWithHigherPrecision =
// BigDecimalUtils.multiply(0.05, totalAmount, false);
// BigDecimal payonusBalanceWithHigherPrecision = BigDecimalUtils.multiply(0.20,
// totalAmount, false)
// .add(BigDecimalUtils.multiply(0.02, totalAmount));

// System.out.println(merchantBalanceWithHigherPrecision + " -> " +
// merchantBalance + ", Diff -> "
// + merchantBalance.subtract(merchantBalanceWithHigherPrecision));
// System.out.println(providerBalanceWithHigherPrecision + " -> " +
// providerBalance + ", Diff -> "
// + providerBalance.subtract(providerBalanceWithHigherPrecision));
// System.out.println(payonusBalanceWithHigherPrecision + " -> " +
// payonusBalance + ", Diff -> "
// + payonusBalance.subtract(payonusBalanceWithHigherPrecision));
// }

// private List<WalletTransaction> generateTransactionsForCredit(BigDecimal
// amount) {
// List<WalletTransaction> transactions = new ArrayList<>();
// WalletTransaction merchantCredit = WalletTransaction.builder()
// .accountNumber(MERCHANT_ACCT)
// .amount(BigDecimalUtils.multiply(0.75, amount))
// .currency(Currency.NGN)
// .reference(UUID.randomUUID().toString())
// .transactionType(TransactionType.CREDIT)
// .build();

// WalletTransaction providerCredit = WalletTransaction.builder()
// .accountNumber(PROVIDER_ACCT)
// .amount(BigDecimalUtils.multiply(0.05, amount))
// .currency(Currency.NGN)
// .reference(merchantCredit.getReference() + "_PROVIDER_FEE")
// .transactionType(TransactionType.CREDIT)
// .build();

// WalletTransaction payonusCredit = WalletTransaction.builder()
// .accountNumber(PAYONUS_ACCT)
// .amount(BigDecimalUtils.multiply(0.20, amount))
// .currency(Currency.NGN)
// .reference(merchantCredit.getReference() + "_FEE")
// .transactionType(TransactionType.CREDIT)
// .build();

// transactions.add(payonusCredit);
// transactions.add(providerCredit);
// transactions.add(merchantCredit);

// return transactions;
// }

// private List<WalletTransaction> generateTransactionsForDebit(BigDecimal
// amount) {
// List<WalletTransaction> transactions = new ArrayList<>();
// WalletTransaction merchantCredit = WalletTransaction.builder()
// .accountNumber(MERCHANT_ACCT)
// .amount(amount)
// .currency(Currency.NGN)
// .reference(UUID.randomUUID() + "_DEBIT")
// .transactionType(TransactionType.DEBIT)
// .build();

// WalletTransaction payonusCredit = WalletTransaction.builder()
// .accountNumber(PAYONUS_ACCT)
// .amount(amount)
// .currency(Currency.NGN)
// .reference(merchantCredit.getReference() + "_CREDIT")
// .transactionType(TransactionType.CREDIT)
// .build();

// transactions.add(payonusCredit);
// transactions.add(merchantCredit);

// return transactions;
// }

// static class Initializer
// implements ApplicationContextInitializer<ConfigurableApplicationContext> {
// public void initialize(ConfigurableApplicationContext
// configurableApplicationContext) {
// TestPropertyValues.of(
// "spring.datasource.url=" + postgres.getJdbcUrl(),
// "spring.datasource.username=" + postgres.getUsername(),
// "spring.datasource.password=" + postgres.getPassword())
// .applyTo(configurableApplicationContext.getEnvironment());
// }
// }

// }
