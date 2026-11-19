package com.example.bank.repository;
import com.example.bank.entity.KycRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface KycRepository extends JpaRepository<KycRecord, Long> {
    Optional<KycRecord> findByUserEmail(String email);
}
