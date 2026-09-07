package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductStoreWarehouseRepository
    implements FulfillmentStore, PanacheRepository<DbProductStoreWarehouse> {

  @Override
  public List<Fulfillment> getAll() {
    return listAll().stream().map(DbProductStoreWarehouse::toFulfillment).toList();
  }

  @Override
  public void create(Fulfillment assignment) {
    DbProductStoreWarehouse dbAssignment = new DbProductStoreWarehouse();
    dbAssignment.productId = assignment.productId;
    dbAssignment.storeId = assignment.storeId;
    dbAssignment.businessUnitCode = assignment.businessUnitCode;

    persist(dbAssignment);

    assignment.id = dbAssignment.id;
  }

  @Override
  public boolean existsExactAssignment(Long productId, Long storeId, String businessUnitCode) {
    return count(
            "productId = ?1 and storeId = ?2 and businessUnitCode = ?3",
            productId,
            storeId,
            businessUnitCode)
        > 0;
  }

  @Override
  public long countWarehousesForProductAndStore(Long productId, Long storeId) {
    return count("productId = ?1 and storeId = ?2", productId, storeId);
  }

  @Override
  public long countDistinctWarehousesForStore(Long storeId) {
    return list("storeId = ?1", storeId).stream()
        .map(assignment -> assignment.businessUnitCode)
        .distinct()
        .count();
  }

  @Override
  public long countDistinctProductsForWarehouse(String businessUnitCode) {
    return list("businessUnitCode = ?1", businessUnitCode).stream()
        .map(assignment -> assignment.productId)
        .distinct()
        .count();
  }
}