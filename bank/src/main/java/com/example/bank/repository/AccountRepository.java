package com.example.bank.repository;

import com.example.bank.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserEmail(String email);
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByUpiId(String upiId);
    Optional<Account> findByUpiIdIgnoreCase(String upiId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :no")
    Optional<Account> findForUpdate(@Param("no") String accountNumber);
}
