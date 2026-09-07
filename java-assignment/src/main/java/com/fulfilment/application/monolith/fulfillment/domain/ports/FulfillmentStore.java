package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import java.util.List;

public interface FulfillmentStore {

  List<Fulfillment> getAll();

  void create(Fulfillment assignment);

  boolean existsExactAssignment(Long productId, Long storeId, String businessUnitCode);

  long countWarehousesForProductAndStore(Long productId, Long storeId);

  long countDistinctWarehousesForStore(Long storeId);

  long countDistinctProductsForWarehouse(String businessUnitCode);
}