package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link StoreResource} no longer calls {@link LegacyStoreManagerGateway} directly,
 * and that the {@link StoreLegacySyncObserver} only propagates a change to the legacy system once
 * the database transaction has actually committed.
 */
@QuarkusTest
public class StoreLegacySyncTest {

  @InjectMock LegacyStoreManagerGateway legacyStoreManagerGateway;

  @BeforeEach
  public void resetMock() {
    reset(legacyStoreManagerGateway);
  }

  @Test
  public void testLegacyGatewayCalledAfterSuccessfulCreate() {
    String newStoreJson = "{\"name\":\"LEGACY-SYNC-CREATE\",\"quantityProductsInStock\":3}";

    given()
        .contentType("application/json")
        .body(newStoreJson)
        .when()
        .post("store")
        .then()
        .statusCode(201);

    verify(legacyStoreManagerGateway, times(1)).createStoreOnLegacySystem(any(Store.class));
    verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any(Store.class));
  }

  @Test
  public void testLegacyGatewayCalledAfterSuccessfulUpdate() {
    String updateJson = "{\"name\":\"KALLAX\",\"quantityProductsInStock\":42}";

    given()
        .contentType("application/json")
        .body(updateJson)
        .when()
        .put("store/2")
        .then()
        .statusCode(200);

    verify(legacyStoreManagerGateway, times(1)).updateStoreOnLegacySystem(any(Store.class));
    verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any(Store.class));
  }

  @Test
  public void testLegacyGatewayNotCalledWhenCreateFailsDueToDuplicateName() {
    // "TONSTAD" is seeded on startup and Store.name is unique, so this create is guaranteed
    // to fail when the transaction tries to commit.
    String duplicateNameJson = "{\"name\":\"TONSTAD\",\"quantityProductsInStock\":1}";

    given()
        .contentType("application/json")
        .body(duplicateNameJson)
        .when()
        .post("store")
        .then()
        .statusCode(500);

    verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any(Store.class));
    verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any(Store.class));
  }
}
