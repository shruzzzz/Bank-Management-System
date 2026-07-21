package com.bank.management.controller;

import com.bank.management.model.Account;
import com.bank.management.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final AccountService accountService;

    public DashboardController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        List<Account> accounts = accountService.getAccountsForUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("accounts", accounts);
        return "dashboard";
    }
}
