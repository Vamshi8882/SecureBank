package com.example.bank.repository;

import com.example.bank.entity.Account;
import com.example.bank.entity.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    Page<BankTransaction> findByFromAccountOrToAccountOrderByCreatedAtDescIdDesc(
            Account from, Account to, Pageable pageable);
}
