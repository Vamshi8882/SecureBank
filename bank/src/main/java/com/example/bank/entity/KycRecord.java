package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name="kyc_records", indexes=@Index(name="idx_kyc_user", columnList="user_id"))
@Getter @Setter @NoArgsConstructor
public class KycRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false, unique=true)
    private User user;
    @Column(nullable=false, length=30)
    private String documentType;
    // Store only a suffix for a demo workflow. Never persist full Aadhaar/PAN identifiers here.
    @Column(nullable=false, length=4)
    private String documentLast4;
    @Column(nullable=false, length=20)
    private String status="PENDING";
    @Column(length=500)
    private String reviewNote;
    @Column(nullable=false, updatable=false)
    private Instant submittedAt=Instant.now();
    private Instant reviewedAt;
}
