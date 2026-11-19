package com.example.bank.service;

import com.example.bank.dto.Dtos.*;
import com.example.bank.entity.Account;
import com.example.bank.entity.User;
import com.example.bank.exception.ApiException;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.UserRepository;
import com.example.bank.security.JwtService;
import com.example.bank.security.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final LoginAttemptService attempts;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        User user = new User();
        user.setFullName(req.fullName().trim());
        user.setEmail(email);
        user.setPhone(req.phone() == null || req.phone().isBlank() ? null : req.phone().trim());
        user.setPasswordHash(encoder.encode(req.password()));
        users.save(user);

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(newAccountNumber());
        account.setUpiId(newUpiId(email));
        accounts.save(account);
        return new AuthResponse(jwt.generate(email), user.getFullName(), account.getAccountNumber(), account.getUpiId());
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        attempts.checkAllowed(email);
        User user = users.findByEmail(email).orElse(null);
        if (user == null || !encoder.matches(req.password(), user.getPasswordHash())) {
            attempts.recordFailure(email);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        attempts.reset(email);
        Account account = accounts.findByUserEmail(email).orElseThrow();
        return new AuthResponse(jwt.generate(email), user.getFullName(), account.getAccountNumber(), account.getUpiId());
    }

    private String newAccountNumber() {
        String number;
        do { number = String.valueOf(1_000_000_000L + (long)(random.nextDouble()*8_999_999_999L)); }
        while (accounts.existsByAccountNumber(number));
        return number;
    }

    private String newUpiId(String email) {
        String base = email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9._-]", "");
        if (base.length() < 3) base = "user" + base;
        String candidate;
        do { candidate = base + random.nextInt(10000) + "@securebank"; }
        while (accounts.existsByUpiId(candidate));
        return candidate;
    }
}
