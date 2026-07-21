package com.bank.management.controller;

import com.bank.management.model.Account;
import com.bank.management.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class TransactionController {

    private final AccountService accountService;

    public TransactionController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ---- Shared helper: confirms login and that the account id in the URL really belongs to this user ----
    private Account requireOwnedAccount(HttpSession session, Long accountId) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new SecurityException("Not logged in");
        }
        Account account = accountService.getAccountOrThrow(accountId);
        if (!accountService.accountBelongsToUser(account, username)) {
            throw new SecurityException("This account does not belong to you");
        }
        return account;
    }

    @GetMapping("/account/{accountId}/deposit")
    public String depositPage(@PathVariable Long accountId, HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("account", requireOwnedAccount(session, accountId));
        return "deposit";
    }

    @PostMapping("/account/{accountId}/deposit")
    public String doDeposit(@PathVariable Long accountId,
                             @RequestParam BigDecimal amount,
                             HttpSession session, Model model) {
        Account account = requireOwnedAccount(session, accountId);
        try {
            accountService.deposit(account, amount, "Cash deposit");
            model.addAttribute("success", "Deposited $" + amount + " successfully.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("account", accountService.getAccountOrThrow(accountId));
        return "deposit";
    }

    @GetMapping("/account/{accountId}/withdraw")
    public String withdrawPage(@PathVariable Long accountId, HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("account", requireOwnedAccount(session, accountId));
        return "withdraw";
    }

    @PostMapping("/account/{accountId}/withdraw")
    public String doWithdraw(@PathVariable Long accountId,
                              @RequestParam BigDecimal amount,
                              HttpSession session, Model model) {
        Account account = requireOwnedAccount(session, accountId);
        try {
            accountService.withdraw(account, amount, "Cash withdrawal");
            model.addAttribute("success", "Withdrew $" + amount + " successfully.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("account", accountService.getAccountOrThrow(accountId));
        return "withdraw";
    }

    @GetMapping("/account/{accountId}/transfer")
    public String transferPage(@PathVariable Long accountId, HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("account", requireOwnedAccount(session, accountId));
        return "transfer";
    }

    @PostMapping("/account/{accountId}/transfer")
    public String doTransfer(@PathVariable Long accountId,
                              @RequestParam String toAccountNumber,
                              @RequestParam BigDecimal amount,
                              HttpSession session, Model model) {
        Account account = requireOwnedAccount(session, accountId);
        try {
            accountService.transfer(account, toAccountNumber, amount);
            model.addAttribute("success", "Transferred $" + amount + " to " + toAccountNumber + ".");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("account", accountService.getAccountOrThrow(accountId));
        return "transfer";
    }

    @GetMapping("/account/{accountId}/history")
    public String history(@PathVariable Long accountId, HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        Account account = requireOwnedAccount(session, accountId);
        model.addAttribute("account", account);
        model.addAttribute("transactions", accountService.getHistory(account));
        return "history";
    }
}
