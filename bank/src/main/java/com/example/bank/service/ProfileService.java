package com.example.bank.service;

import com.example.bank.dto.Dtos.*;
import com.example.bank.entity.User;
import com.example.bank.exception.ApiException;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.KycRepository;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository users;
    private final AccountRepository accounts;
    private final KycRepository kycs;

    @Transactional(readOnly=true)
    public ProfileResponse get(String email) {
        User u = user(email);
        var a = accounts.findByUserEmail(email).orElseThrow();
        String kyc = kycs.findByUserEmail(email).map(k -> k.getStatus()).orElse("NOT_SUBMITTED");
        return new ProfileResponse(u.getFullName(), u.getEmail(), u.getPhone(), u.getAddress(),
                u.getDateOfBirth(), u.getProfilePhotoUrl(), a.getAccountNumber(), a.getUpiId(), kyc);
    }

    @Transactional
    public ProfileResponse update(String email, ProfileUpdateRequest req) {
        User u = user(email);
        u.setFullName(req.fullName().trim());
        u.setPhone(req.phone() == null || req.phone().isBlank() ? null : req.phone().trim());
        u.setAddress(req.address());
        u.setDateOfBirth(req.dateOfBirth());
        u.setProfilePhotoUrl(req.profilePhotoUrl());
        return get(email);
    }

    private User user(String email) {
        return users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
