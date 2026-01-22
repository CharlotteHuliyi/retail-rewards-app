package com.retailer.rewards.reward_app.controller;

import com.retailer.rewards.reward_app.dto.RewardsResponse;
import com.retailer.rewards.reward_app.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardsResponse> getRewards(@PathVariable("customerId") Long customerId) {
        RewardsResponse response = rewardService.getRewardsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
}
