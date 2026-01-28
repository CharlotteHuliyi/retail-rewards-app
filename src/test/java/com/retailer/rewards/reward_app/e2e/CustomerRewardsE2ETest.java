package com.retailer.rewards.reward_app.e2e;

import com.retailer.rewards.reward_app.entity.Customer;
import com.retailer.rewards.reward_app.entity.Transaction;
import com.retailer.rewards.reward_app.repository.CustomerRepo;
import com.retailer.rewards.reward_app.repository.TransactionRepo;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerRewardsE2ETest {

    @LocalServerPort
    private int port; // Automatically injects the random port assigned to Tomcat

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @BeforeEach
    public void setup() {
        // Set the base URI for RestAssured so we don't have to repeat it
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Clean database state
        transactionRepo.deleteAll();
        customerRepo.deleteAll();
    }

    @Test
    public void getRewardsFromExistingCustomer_E2E() {
        // 1. Arrange: Setup real data in the database
        Customer customer = new Customer(null, "John", "Doe");
        customer = customerRepo.save(customer);
        Long id = customer.getCustomerId();

        LocalDate now = LocalDate.now();

        // $120.0 -> 90 points
        Transaction t1 = new Transaction(null, 120.0, now, customer);
        transactionRepo.saveAllAndFlush(List.of(t1));

        // 2. Act & Assert: Call the system through the real network port
        given()
                .accept("application/json")
                .when()
                .get("/rewards/" + id)
                .prettyPeek() // Prints the full request/response to the console like your screenshot
                .then()
                .statusCode(200)
                .body("customerId", equalTo(id.intValue()))
                .body("totalPoints", equalTo(90))
                .body("pointsPerMonth." + now.getMonth().name(), equalTo(90));
    }

    @Test
    public void getRewardsFromNoneExistingCustomer_E2E() {
        given()
                .accept("application/json")
                .when()
                .get("/rewards/999")
                .prettyPeek()
                .then()
                .statusCode(404)
                .body("message", equalTo("Customer not found with id 999"));
    }
}