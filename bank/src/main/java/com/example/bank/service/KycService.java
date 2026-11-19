package com.example.bank.service;
import com.example.bank.dto.Dtos.*;
import com.example.bank.entity.KycRecord;
import com.example.bank.entity.User;
import com.example.bank.exception.ApiException;
import com.example.bank.repository.KycRepository;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KycService {
    private final KycRepository kycs;
    private final UserRepository users;

    @Transactional
    public KycResponse submit(String email, KycSubmitRequest req) {
        KycRecord k = kycs.findByUserEmail(email).orElseGet(() -> {
            KycRecord n = new KycRecord();
            n.setUser(users.findByEmail(email).orElseThrow());
            return n;
        });
        k.setDocumentType(req.documentType());
        k.setDocumentLast4(req.documentLast4().toUpperCase());
        k.setStatus("PENDING");
        k.setReviewNote(null);
        k.setReviewedAt(null);
        return response(kycs.save(k));
    }

    @Transactional(readOnly=true)
    public KycResponse mine(String email) {
        return kycs.findByUserEmail(email).map(this::response)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No KYC submission found"));
    }

    @Transactional(readOnly=true)
    public java.util.List<KycResponse> allForReview() {
        return kycs.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "submittedAt"))
                .stream().map(this::response).toList();
    }

    @Transactional
    public KycResponse review(Long id, KycReviewRequest req) {
        KycRecord k = kycs.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "KYC record not found"));
        k.setStatus(req.status());
        k.setReviewNote(req.reviewNote());
        k.setReviewedAt(Instant.now());
        return response(k);
    }

    private KycResponse response(KycRecord k) {
        return new KycResponse(k.getId(), k.getStatus(), k.getDocumentType(), "•••• " + k.getDocumentLast4(),
                k.getReviewNote(), k.getSubmittedAt(), k.getReviewedAt());
    }
}
