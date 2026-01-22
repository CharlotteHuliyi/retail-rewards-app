package com.retailer.rewards.reward_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class RewardsResponse {
    private Long customerId;
    private Long totalPoints;
    private Map<String,Long> pointsPerMonth;
}
