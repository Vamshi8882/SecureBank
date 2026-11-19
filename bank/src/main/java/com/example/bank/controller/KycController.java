package com.example.bank.controller;
import com.example.bank.dto.Dtos.*;
import com.example.bank.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {
    private final KycService service;
    @GetMapping public KycResponse mine(Authentication auth) { return service.mine(auth.getName()); }
    @PostMapping public KycResponse submit(Authentication auth, @Valid @RequestBody KycSubmitRequest req) {
        return service.submit(auth.getName(), req);
    }
}
