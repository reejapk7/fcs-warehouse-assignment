package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseEndpointTest {

  @Test
  public void testListAndGetWarehouse() {
    final String path = "warehouse";

    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));

    given()
        .when()
        .get(path + "/MWH.001")
        .then()
        .statusCode(200)
        .body(containsString("ZWOLLE-001"));

    given().when().get(path + "/DOES-NOT-EXIST").then().statusCode(404);
  }

  @Test
  public void testCreateWarehouse() {
    final String path = "warehouse";

    String newWarehouseJson =
        "{\"businessUnitCode\":\"MWH.100\",\"location\":\"AMSTERDAM-002\",\"capacity\":20,\"stock\":5}";

    given()
        .contentType("application/json")
        .body(newWarehouseJson)
        .when()
        .post(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.100"));

    // Duplicate business unit code should be rejected:
    given()
        .contentType("application/json")
        .body(newWarehouseJson)
        .when()
        .post(path)
        .then()
        .statusCode(400);

    // Capacity exceeding the location's maximum should be rejected:
    String tooBigJson =
        "{\"businessUnitCode\":\"MWH.101\",\"location\":\"ZWOLLE-002\",\"capacity\":999,\"stock\":0}";

    given()
        .contentType("application/json")
        .body(tooBigJson)
        .when()
        .post(path)
        .then()
        .statusCode(400);
  }

  @Test
  public void testReplaceWarehouseValidationRejections() {
    // No active warehouse for this business unit code should be rejected:
    given()
        .contentType("application/json")
        .body("{\"location\":\"TILBURG-001\",\"capacity\":35,\"stock\":27}")
        .when()
        .post("warehouse/DOES-NOT-EXIST/replacement")
        .then()
        .statusCode(400);

    // Missing stock should be rejected:
    given()
        .contentType("application/json")
        .body("{\"location\":\"TILBURG-001\",\"capacity\":35}")
        .when()
        .post("warehouse/MWH.023/replacement")
        .then()
        .statusCode(400);

    // Stock exceeding the new capacity should be rejected:
    given()
        .contentType("application/json")
        .body("{\"location\":\"TILBURG-001\",\"capacity\":10,\"stock\":100}")
        .when()
        .post("warehouse/MWH.023/replacement")
        .then()
        .statusCode(400);

    // New capacity unable to accommodate the old stock (27) should be rejected:
    given()
        .contentType("application/json")
        .body("{\"location\":\"TILBURG-001\",\"capacity\":20,\"stock\":20}")
        .when()
        .post("warehouse/MWH.023/replacement")
        .then()
        .statusCode(400);

    // New stock not matching the old stock (27) should be rejected:
    given()
        .contentType("application/json")
        .body("{\"location\":\"TILBURG-001\",\"capacity\":35,\"stock\":10}")
        .when()
        .post("warehouse/MWH.023/replacement")
        .then()
        .statusCode(400);

    // Replacement location already at its maximum warehouse count should be rejected:
    given()
        .contentType("application/json")
        .body("{\"location\":\"ZWOLLE-001\",\"capacity\":35,\"stock\":27}")
        .when()
        .post("warehouse/MWH.023/replacement")
        .then()
        .statusCode(400);
  }

  @Test
  public void testArchiveWarehouse() {
    final String path = "warehouse";

    given().when().delete(path + "/MWH.012").then().statusCode(204);

    given().when().delete(path + "/DOES-NOT-EXIST").then().statusCode(404);
  }
}
