package com.bank.management;

import com.bank.management.model.Account;
import com.bank.management.model.User;
import com.bank.management.repository.AccountRepository;
import com.bank.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class BankManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankManagementApplication.class, args);
    }

    /**
     * Runs once at startup to create a demo user + account so you have
     * something to log in with immediately (username: demo / password: demo123).
     */
    @Bean
    CommandLineRunner seedDemoData(UserRepository userRepository, AccountRepository accountRepository) {
        return args -> {
            if (userRepository.findByUsername("demo").isEmpty()) {
                User demoUser = new User();
                demoUser.setUsername("demo");
                demoUser.setPassword("demo123"); // plain text for learning purposes only, see README
                demoUser.setFullName("Demo User");
                demoUser.setEmail("demo@example.com");
                userRepository.save(demoUser);

                Account demoAccount = new Account();
                demoAccount.setAccountNumber("ACC1000001");
                demoAccount.setOwner(demoUser);
                demoAccount.setBalance(new BigDecimal("5000.00"));
                demoAccount.setAccountType("SAVINGS");
                accountRepository.save(demoAccount);
            }
        };
    }
}
