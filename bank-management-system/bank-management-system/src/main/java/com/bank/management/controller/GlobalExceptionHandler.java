package com.bank.management.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public String handleSecurityException() {
        return "redirect:/login";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument() {
        return "redirect:/dashboard";
    }
}
