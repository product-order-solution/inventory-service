package com.techie.microservices.inventory_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


// The @Import annotation explicitly includes the TestcontainersConfiguration class in the test, making the
// mysqlContainer available as a bean.
// @SpringBootTest annotation tells Spring Boot to start the application context, and to run in a random port.
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	// Inject the MySQLContainer bean
	@Autowired
	private MySQLContainer<?> mysqlContainer;

	//When the test run, the port chosen will be added to this variable.
	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		// Configuration for RestAssured.
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}


	@Test
	void contextLoads() {
		// Verify that the container is running
		assert mysqlContainer.isRunning();

		// You can also access other container information, e.g.:
		System.out.println("MySQL JDBC URL: " + mysqlContainer.getJdbcUrl());
		System.out.println("MySQL Username: " + mysqlContainer.getUsername());
		System.out.println("MySQL Password: " + mysqlContainer.getPassword());
	}

	@Test
	void shouldReadInventory() {

		var response = RestAssured.given()
				.when()
				.get("/api/inventory?skuCode=iphone_15&quantity=1")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertTrue(response);

		var negativeResponse = RestAssured.given()
				.when()
				.get("/api/inventory?skuCode=iphone_15&quantity=101")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertFalse(negativeResponse);

	}

}
