package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="loan_applications", indexes=@Index(name="idx_loan_user", columnList="user_id"))
@Getter @Setter @NoArgsConstructor
public class LoanApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Column(nullable=false, length=20)
    private String loanType;
    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal requestedAmount;
    @Column(nullable=false)
    private Integer tenureMonths;
    @Column(nullable=false, length=1000)
    private String purpose;
    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal monthlyIncome;
    @Column(nullable=false, length=20)
    private String status="SUBMITTED";
    @Column(length=500)
    private String reviewNote;
    @Column(nullable=false, updatable=false)
    private Instant appliedAt=Instant.now();
    private Instant reviewedAt;
}
