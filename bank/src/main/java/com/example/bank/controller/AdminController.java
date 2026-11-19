package com.example.bank.controller;

import com.example.bank.dto.Dtos.*;
import com.example.bank.service.KycService;
import com.example.bank.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final KycService kycService;
    private final LoanService loanService;

    @GetMapping("/kyc")
    public java.util.List<KycResponse> allKyc() { return kycService.allForReview(); }

    @GetMapping("/loans")
    public java.util.List<LoanResponse> allLoans() { return loanService.allForReview(); }

    @PutMapping("/kyc/{id}")
    public KycResponse reviewKyc(@PathVariable Long id, @Valid @RequestBody KycReviewRequest req) {
        return kycService.review(id, req);
    }

    public record LoanReviewRequest(@NotBlank String status, @Size(max=500) String reviewNote) {}

    @PutMapping("/loans/{id}")
    public LoanResponse reviewLoan(@PathVariable Long id, @Valid @RequestBody LoanReviewRequest req) {
        return loanService.review(id, req.status(), req.reviewNote());
    }
}
