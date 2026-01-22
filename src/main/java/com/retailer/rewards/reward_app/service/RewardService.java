package com.retailer.rewards.reward_app.service;

import com.retailer.rewards.reward_app.dto.RewardsResponse;
import com.retailer.rewards.reward_app.entity.Transaction;
import com.retailer.rewards.reward_app.exception.CustomerNotFoundException;
import com.retailer.rewards.reward_app.repository.CustomerRepo;
import com.retailer.rewards.reward_app.repository.TransactionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardService {
    private final TransactionRepo transactionRepo;

    private final CustomerRepo customerRepo;

    public RewardService(TransactionRepo transactionRepo, CustomerRepo customerRepo) {
        this.transactionRepo = transactionRepo;
        this.customerRepo = customerRepo;
    }

    public RewardsResponse getRewardsByCustomerId (Long id){
        // check customer existence
        if (!customerRepo.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        // get transactions for last three months
        LocalDate now = LocalDate.now();
        LocalDate threeMonthsAgo = now.minusMonths(3);

        List<Transaction> transactions = transactionRepo.findAllByCustomer_CustomerIdAndDateBetween(id, threeMonthsAgo, now);

        // calculate points/month & total points
        Map<String, Long> pointsPerMonth = new HashMap<>();
        long totalPoints = 0L;
        for (Transaction transaction : transactions) {
            Long points = calculatePoints(transaction.getAmount());
            String month = transaction.getDate().getMonth().toString();
            pointsPerMonth.put(month,pointsPerMonth.getOrDefault(month, 0L) + points);
            totalPoints += points;
        }

//        Map<String, Object> result = new HashMap<>();
//        result.put("customerId", id);
//        result.put("pointsPerMonth", pointsPerMonth);
//        result.put("totalPoints", totalPoints);
//        //result.put("transactions", transactions);

 //       return result;
        return new RewardsResponse(id, totalPoints, pointsPerMonth);
    }

    public Long calculatePoints(Double amount) {
        if (amount <= 50) return 0L;

        long points = 0L;
        if (amount > 100) {
            long twoPointsReward =  Math.round ((amount - 100) * 2);
            points = twoPointsReward + 50;
        } else {
            points = Math.round(amount - 50);
        }
        return points;
    }



}
