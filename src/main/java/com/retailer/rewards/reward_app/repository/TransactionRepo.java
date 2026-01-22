package com.retailer.rewards.reward_app.repository;

import com.retailer.rewards.reward_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCustomer_CustomerIdAndDateBetween(Long customerId, LocalDate from, LocalDate to);
}
