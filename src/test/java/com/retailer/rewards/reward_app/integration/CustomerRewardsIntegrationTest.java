package com.retailer.rewards.reward_app.integration;

import com.retailer.rewards.reward_app.entity.Customer;
import com.retailer.rewards.reward_app.entity.Transaction;
import com.retailer.rewards.reward_app.repository.CustomerRepo;
import com.retailer.rewards.reward_app.repository.TransactionRepo;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerRewardsIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        // Clean up database (reverse order of dependency)
        transactionRepo.deleteAll();
        customerRepo.deleteAll();
    }

    @Test
    public void testGetRewards_Integration_Success() {
        // 1. Arrange: Create and save a real Customer
        Customer customer = new Customer(null, "John", "Doe");
        customer = customerRepo.save(customer);
        Long id = customer.getCustomerId();

        // 2. Arrange: Use dynamic dates to satisfy the "Last 3 Months" service logic
        LocalDate currentMonth = LocalDate.now();
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        // $120.0 -> (20 * 2) + 50 = 90 points
        // $80.0  -> (30 * 1) = 30 points
        Transaction t1 = new Transaction(null, 120.0, currentMonth, customer);
        Transaction t2 = new Transaction(null, 80.0, lastMonth, customer);

        // Use saveAllAndFlush to ensure H2 has the data before the API call
        transactionRepo.saveAllAndFlush(List.of(t1, t2));

        // 3. Act & Assert
        given()
                .accept("application/json")
                .when()
                .get("/rewards/" + id)
                .then()
                .log().body()
                .statusCode(200)
                .body("customerId", equalTo(id.intValue()))
                .body("totalPoints", equalTo(120))
                .body("pointsPerMonth." + currentMonth.getMonth().name(), equalTo(90))
                .body("pointsPerMonth." + lastMonth.getMonth().name(), equalTo(30));
    }

    @Test
    public void testGetRewards_Integration_NotFound() {
        given()
                .accept("application/json")
                .when()
                .get("/rewards/999")
                .then()
                .log().all()
                .statusCode(404)
                .body("message", equalTo("Customer not found with id 999"));
    }
}