package com.example.bank.controller;

import com.example.bank.dto.Dtos.*;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public AccountResponse balance(Authentication auth) { return accountService.getAccount(auth.getName()); }

    @PostMapping("/deposit")
    public AccountResponse deposit(Authentication auth, @Valid @RequestBody AmountRequest req) {
        return accountService.deposit(auth.getName(), req);
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(Authentication auth, @Valid @RequestBody TransferRequest req) {
        return accountService.transfer(auth.getName(), req);
    }

    @PostMapping("/upi-transfer")
    public TransactionResponse upiTransfer(Authentication auth, @Valid @RequestBody UpiTransferRequest req) {
        return accountService.upiTransfer(auth.getName(), req);
    }

    @GetMapping("/transactions")
    public PageResponse<TransactionResponse> history(Authentication auth,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return accountService.history(auth.getName(), page, size);
    }
}
