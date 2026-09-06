package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FulfillmentEndpointTest {

  @Test
  public void testCreateAndListFulfillment() {
    final String path = "fulfillment";

    // product 2 (KALLAX), store 2 (KALLAX), warehouse MWH.012 all exist from the seed data:
    String requestJson =
        "{\"productId\":2,\"storeId\":2,\"businessUnitCode\":\"MWH.012\"}";

    given()
        .contentType("application/json")
        .body(requestJson)
        .when()
        .post(path)
        .then()
        .statusCode(201)
        .body(containsString("MWH.012"));

    // List all, the new assignment should be present:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.012"));
  }

  @Test
  public void testCreateExactDuplicateAssignmentReturns400() {
    String requestJson =
        "{\"productId\":3,\"storeId\":3,\"businessUnitCode\":\"MWH.023\"}";

    given()
        .contentType("application/json")
        .body(requestJson)
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);

    // The exact same assignment again should be rejected:
    given()
        .contentType("application/json")
        .body(requestJson)
        .when()
        .post("fulfillment")
        .then()
        .statusCode(400);
  }

  @Test
  public void testCreateWithNonExistingProductReturns400() {
    String requestJson =
        "{\"productId\":999999,\"storeId\":1,\"businessUnitCode\":\"MWH.001\"}";

    given()
        .contentType("application/json")
        .body(requestJson)
        .when()
        .post("fulfillment")
        .then()
        .statusCode(400);
  }

  @Test
  public void testCreateWithNonExistingStoreReturns400() {
    String requestJson =
        "{\"productId\":1,\"storeId\":999999,\"businessUnitCode\":\"MWH.001\"}";

    given()
        .contentType("application/json")
        .body(requestJson)
        .when()
        .post("fulfillment")
        .then()
        .statusCode(400);
  }

  @Test
  public void testCreateWithNonExistingWarehouseReturns400() {
    String requestJson =
        "{\"productId\":1,\"storeId\":1,\"businessUnitCode\":\"MWH.999\"}";

    given()
        .contentType("application/json")
        .body(requestJson)
        .when()
        .post("fulfillment")
        .then()
        .statusCode(400);
  }
}
