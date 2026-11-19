package com.example.bank.service;
import com.example.bank.dto.Dtos.*;
import com.example.bank.entity.LoanApplication;
import com.example.bank.exception.ApiException;
import com.example.bank.repository.LoanRepository;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loans;
    private final UserRepository users;

    @Transactional
    public LoanResponse apply(String email, LoanApplyRequest req) {
        LoanApplication l = new LoanApplication();
        l.setUser(users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found")));
        l.setLoanType(req.loanType());
        l.setRequestedAmount(req.requestedAmount());
        l.setTenureMonths(req.tenureMonths());
        l.setPurpose(req.purpose().trim());
        l.setMonthlyIncome(req.monthlyIncome());
        return response(loans.save(l));
    }

    @Transactional(readOnly=true)
    public java.util.List<LoanResponse> mine(String email) {
        return loans.findByUserEmailOrderByAppliedAtDesc(email).stream().map(this::response).toList();
    }

    @Transactional(readOnly=true)
    public java.util.List<LoanResponse> allForReview() {
        return loans.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "appliedAt"))
                .stream().map(this::response).toList();
    }

    @Transactional
    public LoanResponse review(Long id, String status, String note) {
        if (!java.util.Set.of("UNDER_REVIEW","APPROVED","REJECTED","NEED_MORE_INFO").contains(status))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid loan status");
        LoanApplication l = loans.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Loan application not found"));
        l.setStatus(status); l.setReviewNote(note); l.setReviewedAt(Instant.now());
        return response(l);
    }

    private LoanResponse response(LoanApplication l) {
        return new LoanResponse(l.getId(), l.getLoanType(), l.getRequestedAmount(), l.getTenureMonths(),
                l.getPurpose(), l.getMonthlyIncome(), l.getStatus(), l.getReviewNote(), l.getAppliedAt(), l.getReviewedAt());
    }
}
