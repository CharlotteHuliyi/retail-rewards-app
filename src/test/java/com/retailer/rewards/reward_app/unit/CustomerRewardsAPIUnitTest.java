package com.retailer.rewards.reward_app.unit;

import com.retailer.rewards.reward_app.controller.RewardController;
import com.retailer.rewards.reward_app.dto.RewardsResponse;
import com.retailer.rewards.reward_app.exception.CustomerNotFoundException;
import com.retailer.rewards.reward_app.exception.GlobalExceptionHandler;
import com.retailer.rewards.reward_app.service.RewardService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class CustomerRewardsAPIUnitTest {
    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardController rewardController;

    @BeforeEach
    public void configMock() {
        RestAssuredMockMvc.config = new RestAssuredMockMvcConfig()
                .logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(true)); // <--- This makes the JSON look nice

        RestAssuredMockMvc.standaloneSetup(rewardController, new GlobalExceptionHandler());
    }

    @Test
    public void testGetRewardsByCustomerId() {
        // 1. Arrange: Prepare the fake data
        Map<String, Long> mockPoints = new HashMap<>();
        mockPoints.put("JANUARY", 40L);
        mockPoints.put("FEBRUARY", 90L);
        mockPoints.put("MARCH", 0L);

        RewardsResponse mockResponse = new RewardsResponse(1L, 130L, mockPoints);

        // Tell the mock service what to return
        Mockito.when(rewardService.getRewardsByCustomerId(anyLong()))
                .thenReturn(mockResponse);

        // 2. Act & Assert: The format you love
        given()
                .accept("application/json")
                .when()
                .get("/rewards/1") // Make sure this matches your @RequestMapping
                .peek()
                .then()
                .log().body()
                .statusCode(200)
                .body("customerId", equalTo(1))
                .body("pointsPerMonth.JANUARY", equalTo(40))
                .body("totalPoints", equalTo(130));
    }

    @Test
    public void testGetRewards_CustomerNotFound_Returns404() {
        // 1. Arrange: Ensure the mocked exception matches the real logic
        Mockito.when(rewardService.getRewardsByCustomerId(999L))
                .thenThrow(new CustomerNotFoundException(999L));

        // 2. Act & Assert
        given()
                .accept("application/json")
                .when()
                .get("/rewards/999")
                .then()
                .log().body()
                .statusCode(404)
                // CHANGE THIS LINE to match the actual error message from the logs
                .body("message", equalTo("Customer not found with id 999"))
                .body("status", equalTo(404))
                .body("timestamp", notNullValue());
    }
}