package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  @Test
  public void testCrudProduct() {
    final String path = "product";

    // List all, should have all 3 products the database has initially:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    // Delete the TONSTAD:
    given().when().delete(path + "/1").then().statusCode(204);

    // List all, TONSTAD should be missing now:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(not(containsString("TONSTAD")), containsString("KALLAX"), containsString("BESTÅ"));
  }

  @Test
  public void testGetSingleProduct() {
    given()
        .when()
        .get("product/2")
        .then()
        .statusCode(200)
        .body(containsString("KALLAX"));
  }

  @Test
  public void testGetSingleProductNotFoundReturns404() {
    given().when().get("product/999999").then().statusCode(404);
  }

  @Test
  public void testCreateProduct() {
    String newProductJson =
        "{\"name\":\"MALM\",\"description\":\"Bed frame\",\"price\":199,\"stock\":4}";

    given()
        .contentType("application/json")
        .body(newProductJson)
        .when()
        .post("product")
        .then()
        .statusCode(201)
        .body(containsString("MALM"));
  }

  @Test
  public void testCreateProductWithIdSetReturns422() {
    String invalidJson =
        "{\"id\":1,\"name\":\"INVALID\",\"description\":\"x\",\"price\":1,\"stock\":1}";

    given()
        .contentType("application/json")
        .body(invalidJson)
        .when()
        .post("product")
        .then()
        .statusCode(422);
  }

  @Test
  public void testUpdateProduct() {
    String updateJson =
        "{\"name\":\"KALLAX-2\",\"description\":\"Shelf unit\",\"price\":89,\"stock\":6}";

    given()
        .contentType("application/json")
        .body(updateJson)
        .when()
        .put("product/2")
        .then()
        .statusCode(200)
        .body(containsString("KALLAX-2"));
  }

  @Test
  public void testUpdateProductWithoutNameReturns422() {
    String invalidJson = "{\"description\":\"x\",\"price\":1,\"stock\":1}";

    given()
        .contentType("application/json")
        .body(invalidJson)
        .when()
        .put("product/2")
        .then()
        .statusCode(422);
  }

  @Test
  public void testUpdateNonExistingProductReturns404() {
    String updateJson =
        "{\"name\":\"DOES-NOT-EXIST\",\"description\":\"x\",\"price\":1,\"stock\":1}";

    given()
        .contentType("application/json")
        .body(updateJson)
        .when()
        .put("product/999999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testDeleteNonExistingProductReturns404() {
    given().when().delete("product/999999").then().statusCode(404);
  }
}
