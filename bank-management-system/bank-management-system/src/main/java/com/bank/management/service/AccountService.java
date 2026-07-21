package com.bank.management.service;

import com.bank.management.model.Account;
import com.bank.management.model.Transaction;
import com.bank.management.model.User;
import com.bank.management.repository.AccountRepository;
import com.bank.management.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> getAccountsForUsername(String username) {
        return accountRepository.findByOwnerUsername(username);
    }

    public Account getAccountOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /** Ensures the given account actually belongs to the logged-in user, to prevent tampering with someone else's account id in the URL/form. */
    public boolean accountBelongsToUser(Account account, String username) {
        return account.getOwner().getUsername().equals(username);
    }

    @Transactional
    public void deposit(Account account, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        recordTransaction(account, "DEPOSIT", amount, description);
    }

    @Transactional
    public void withdraw(Account account, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        recordTransaction(account, "WITHDRAW", amount, description);
    }

    @Transactional
    public void transfer(Account from, String toAccountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        Account to = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (to.getId().equals(from.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);

        recordTransaction(from, "TRANSFER_OUT", amount, "To " + to.getAccountNumber());
        recordTransaction(to, "TRANSFER_IN", amount, "From " + from.getAccountNumber());
    }

    public List<Transaction> getHistory(Account account) {
        return transactionRepository.findByAccountOrderByTimestampDesc(account);
    }

    private void recordTransaction(Account account, String type, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setBalanceAfter(account.getBalance());
        transactionRepository.save(transaction);
    }
}
