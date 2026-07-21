package com.bank.management.service;

import com.bank.management.model.Account;
import com.bank.management.model.User;
import com.bank.management.repository.AccountRepository;
import com.bank.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public UserService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(u -> u.getPassword().equals(rawPassword))
                .orElse(false);
    }

    /**
     * Registers a new user and automatically opens a savings account for them
     * with a zero starting balance.
     */
    public User registerUser(String username, String password, String fullName, String email, String accountType) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        User saved = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setOwner(saved);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(accountType);
        accountRepository.save(account);

        return saved;
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String candidate;
        do {
            candidate = "ACC" + (1000000 + random.nextInt(9000000));
        } while (accountRepository.findByAccountNumber(candidate).isPresent());
        return candidate;
    }
}
