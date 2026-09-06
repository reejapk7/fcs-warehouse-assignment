package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple in-memory fake, used to unit test fulfillment business logic without a real database.
 * The counting/lookup methods are computed from the stored assignments, mirroring what the real
 * repository's queries would return.
 */
public class FakeFulfillmentStore implements FulfillmentStore {

  public final List<Fulfillment> assignments = new ArrayList<>();

  @Override
  public List<Fulfillment> getAll() {
    return assignments;
  }

  @Override
  public void create(Fulfillment assignment) {
    assignments.add(assignment);
  }

  @Override
  public boolean existsExactAssignment(Long productId, Long storeId, String businessUnitCode) {
    return assignments.stream()
        .anyMatch(
            a ->
                Objects.equals(a.productId, productId)
                    && Objects.equals(a.storeId, storeId)
                    && Objects.equals(a.businessUnitCode, businessUnitCode));
  }

  @Override
  public long countWarehousesForProductAndStore(Long productId, Long storeId) {
    return assignments.stream()
        .filter(a -> Objects.equals(a.productId, productId) && Objects.equals(a.storeId, storeId))
        .map(a -> a.businessUnitCode)
        .distinct()
        .count();
  }

  @Override
  public long countDistinctWarehousesForStore(Long storeId) {
    return assignments.stream()
        .filter(a -> Objects.equals(a.storeId, storeId))
        .map(a -> a.businessUnitCode)
        .distinct()
        .count();
  }

  @Override
  public long countDistinctProductsForWarehouse(String businessUnitCode) {
    return assignments.stream()
        .filter(a -> Objects.equals(a.businessUnitCode, businessUnitCode))
        .map(a -> a.productId)
        .distinct()
        .count();
  }
}
