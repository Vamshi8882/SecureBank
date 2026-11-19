package com.example.bank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class Dtos {
    private Dtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @Pattern(regexp = "^$|[6-9][0-9]{9}", message = "must be a valid 10-digit Indian mobile number") String phone) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String fullName, String accountNumber, String upiId) {}

    public record AmountRequest(
            @NotNull @DecimalMin("0.01") @DecimalMax("100000.00") @Digits(integer = 9, fraction = 2)
            BigDecimal amount) {}

    public record TransferRequest(
            @NotBlank @Pattern(regexp = "\\d{10}", message = "must be a 10-digit account number")
            String toAccountNumber,
            @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal amount,
            @Size(max = 100) String description) {}

    public record UpiTransferRequest(
            @NotBlank @Email @Size(max = 80) String toUpiId,
            @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal amount,
            @Size(max = 100) String note) {}

    public record AccountResponse(String accountNumber, String upiId, String ownerName, BigDecimal balance) {}
    public record TransactionResponse(Long id, String type, String direction, BigDecimal amount,
                                      String fromAccount, String toAccount, String description, Instant createdAt) {}
    public record PageResponse<T>(List<T> items, int page, int totalPages, long totalItems) {}

    public record ProfileResponse(String fullName, String email, String phone, String address,
                                  LocalDate dateOfBirth, String profilePhotoUrl,
                                  String accountNumber, String upiId, String kycStatus) {}
    public record ProfileUpdateRequest(
            @NotBlank @Size(max=100) String fullName,
            @Pattern(regexp = "^$|[6-9][0-9]{9}", message="must be a valid 10-digit Indian mobile number") String phone,
            @Size(max=300) String address,
            LocalDate dateOfBirth,
            @Size(max=500) String profilePhotoUrl) {}

    public record KycSubmitRequest(
            @NotBlank @Pattern(regexp="AADHAAR|PAN|PASSPORT|VOTER_ID|DRIVING_LICENSE") String documentType,
            @NotBlank @Pattern(regexp="[0-9A-Za-z]{4}", message="provide only the last four characters") String documentLast4) {}
    public record KycResponse(Long id, String status, String documentType, String maskedDocument,
                              String reviewNote, Instant submittedAt, Instant reviewedAt) {}
    public record KycReviewRequest(@NotBlank @Pattern(regexp="APPROVED|REJECTED|PENDING") String status,
                                   @Size(max=500) String reviewNote) {}

    public record LoanApplyRequest(
            @NotBlank @Pattern(regexp="PERSONAL|EDUCATION|HOME|VEHICLE") String loanType,
            @NotNull @DecimalMin("1000.00") @DecimalMax("10000000.00") BigDecimal requestedAmount,
            @NotNull @Min(6) @Max(360) Integer tenureMonths,
            @NotBlank @Size(max=1000) String purpose,
            @NotNull @DecimalMin("0.00") BigDecimal monthlyIncome) {}
    public record LoanResponse(Long id, String loanType, BigDecimal requestedAmount, Integer tenureMonths,
                               String purpose, BigDecimal monthlyIncome, String status,
                               String reviewNote, Instant appliedAt, Instant reviewedAt) {}
}
