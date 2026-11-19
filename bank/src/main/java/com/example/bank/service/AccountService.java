package com.example.bank.service;

import com.example.bank.dto.Dtos.*;
import com.example.bank.entity.Account;
import com.example.bank.entity.BankTransaction;
import com.example.bank.entity.TransactionType;
import com.example.bank.exception.ApiException;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    @Value("${app.demo-deposits-enabled:false}")
    private boolean demoDepositsEnabled;

    @Transactional
    public AccountResponse getAccount(String email) {
        Account a = findByEmail(email);
        if (a.getUpiId() == null || a.getUpiId().isBlank()) {
            String alias = "customer" + a.getAccountNumber().substring(4) + "@securebank";
            a.setUpiId(alias);
        }
        return new AccountResponse(a.getAccountNumber(), a.getUpiId(), a.getUser().getFullName(), a.getBalance());
    }

    @Transactional
    public AccountResponse deposit(String email, AmountRequest req) {
        if (!demoDepositsEnabled) throw new ApiException(HttpStatus.FORBIDDEN, "Demo deposits are disabled");
        Account a = accounts.findForUpdate(findByEmail(email).getAccountNumber()).orElseThrow();
        a.setBalance(a.getBalance().add(req.amount()));
        BankTransaction t = new BankTransaction();
        t.setType(TransactionType.DEPOSIT); t.setAmount(req.amount()); t.setToAccount(a);
        t.setDescription("Demo deposit"); transactions.save(t);
        return new AccountResponse(a.getAccountNumber(), a.getUpiId(), a.getUser().getFullName(), a.getBalance());
    }

    @Transactional
    public TransactionResponse transfer(String email, TransferRequest req) {
        return executeTransfer(email, req.toAccountNumber(), req.amount(), req.description());
    }

    @Transactional
    public TransactionResponse upiTransfer(String email, UpiTransferRequest req) {
        Account destination = accounts.findByUpiIdIgnoreCase(req.toUpiId().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "UPI ID not found"));
        return executeTransfer(email, destination.getAccountNumber(), req.amount(), req.note());
    }

    private TransactionResponse executeTransfer(String email, String destinationNumber,
                                                java.math.BigDecimal amount, String description) {
        String fromNo = findByEmail(email).getAccountNumber();
        String toNo = destinationNumber.trim();
        if (fromNo.equals(toNo)) throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot transfer to your own account");
        String firstNo = fromNo.compareTo(toNo) < 0 ? fromNo : toNo;
        String secondNo = firstNo.equals(fromNo) ? toNo : fromNo;
        Account first = accounts.findForUpdate(firstNo)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Destination account not found"));
        Account second = accounts.findForUpdate(secondNo)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Destination account not found"));
        Account from = first.getAccountNumber().equals(fromNo) ? first : second;
        Account to = from == first ? second : first;
        if (amount == null || amount.signum() <= 0) throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        if (from.getBalance().compareTo(amount) < 0)
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient funds");
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        BankTransaction t = new BankTransaction();
        t.setType(TransactionType.TRANSFER); t.setAmount(amount); t.setFromAccount(from); t.setToAccount(to);
        t.setDescription(description == null ? null : description.trim());
        transactions.save(t);
        return toResponse(t, from);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> history(String email, int page, int size) {
        Account a = findByEmail(email);
        var result = transactions.findByFromAccountOrToAccountOrderByCreatedAtDescIdDesc(
                a, a, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50)));
        return new PageResponse<>(result.getContent().stream().map(t -> toResponse(t, a)).toList(),
                result.getNumber(), result.getTotalPages(), result.getTotalElements());
    }

    private Account findByEmail(String email) {
        return accounts.findByUserEmail(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    private TransactionResponse toResponse(BankTransaction t, Account viewer) {
        boolean debit = t.getFromAccount() != null && t.getFromAccount().getId().equals(viewer.getId());
        return new TransactionResponse(t.getId(), t.getType().name(), debit ? "DEBIT" : "CREDIT", t.getAmount(),
                t.getFromAccount() == null ? null : t.getFromAccount().getAccountNumber(),
                t.getToAccount() == null ? null : t.getToAccount().getAccountNumber(),
                t.getDescription(), t.getCreatedAt());
    }
}
