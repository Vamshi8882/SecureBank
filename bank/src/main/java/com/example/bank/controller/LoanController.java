package com.example.bank.controller;
import com.example.bank.dto.Dtos.*;
import com.example.bank.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService service;
    @PostMapping public LoanResponse apply(Authentication auth, @Valid @RequestBody LoanApplyRequest req) {
        return service.apply(auth.getName(), req);
    }
    @GetMapping public List<LoanResponse> mine(Authentication auth) { return service.mine(auth.getName()); }
}
