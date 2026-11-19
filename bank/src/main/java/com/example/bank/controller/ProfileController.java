package com.example.bank.controller;
import com.example.bank.dto.Dtos.*;
import com.example.bank.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService service;
    @GetMapping public ProfileResponse get(Authentication auth) { return service.get(auth.getName()); }
    @PutMapping public ProfileResponse update(Authentication auth, @Valid @RequestBody ProfileUpdateRequest req) {
        return service.update(auth.getName(), req);
    }
}
