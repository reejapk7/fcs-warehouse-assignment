package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

  @Test
  public void testCrudStore() {
    final String path = "store";

    // List all, should have all 3 stores the database has initially:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    // Get a single store by id:
    given()
        .when()
        .get(path + "/1")
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"));

    // Create a new store:
    String newStoreJson = "{\"name\":\"UTRECHT\",\"quantityProductsInStock\":7}";
    int newId =
        given()
            .contentType("application/json")
            .body(newStoreJson)
            .when()
            .post(path)
            .then()
            .statusCode(201)
            .body(containsString("UTRECHT"))
            .extract()
            .path("id");

    // Replace it (PUT):
    String replaceJson = "{\"name\":\"UTRECHT-2\",\"quantityProductsInStock\":9}";
    given()
        .contentType("application/json")
        .body(replaceJson)
        .when()
        .put(path + "/" + newId)
        .then()
        .statusCode(200)
        .body(containsString("UTRECHT-2"));

    // Partially update it (PATCH):
    String patchJson = "{\"name\":\"UTRECHT-3\",\"quantityProductsInStock\":11}";
    given()
        .contentType("application/json")
        .body(patchJson)
        .when()
        .patch(path + "/" + newId)
        .then()
        .statusCode(200)
        .body(containsString("UTRECHT-3"));

    // Delete it:
    given().when().delete(path + "/" + newId).then().statusCode(204);

    // List all, the new store should be missing now:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(not(containsString("UTRECHT-3")));
  }

  @Test
  public void testGetSingleStoreNotFoundReturns404() {
    given().when().get("store/999999").then().statusCode(404);
  }

  @Test
  public void testCreateStoreWithIdSetReturns422() {
    String invalidJson = "{\"id\":1,\"name\":\"INVALID\",\"quantityProductsInStock\":1}";
    given()
        .contentType("application/json")
        .body(invalidJson)
        .when()
        .post("store")
        .then()
        .statusCode(422);
  }

  @Test
  public void testUpdateStoreWithoutNameReturns422() {
    String invalidJson = "{\"quantityProductsInStock\":1}";
    given()
        .contentType("application/json")
        .body(invalidJson)
        .when()
        .put("store/1")
        .then()
        .statusCode(422);
  }

  @Test
  public void testUpdateNonExistingStoreReturns404() {
    String json = "{\"name\":\"DOES-NOT-EXIST\",\"quantityProductsInStock\":1}";
    given()
        .contentType("application/json")
        .body(json)
        .when()
        .put("store/999999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testDeleteNonExistingStoreReturns404() {
    given().when().delete("store/999999").then().statusCode(404);
  }
}
