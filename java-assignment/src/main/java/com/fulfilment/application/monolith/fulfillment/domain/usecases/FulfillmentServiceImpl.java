package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentService;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductExistenceChecker;
import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreExistenceChecker;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FulfillmentServiceImpl implements FulfillmentService {

  private static final Logger LOGGER = Logger.getLogger(FulfillmentServiceImpl.class.getName());

  private static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  private static final int MAX_WAREHOUSES_PER_STORE = 3;
  private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

  private final FulfillmentStore fulfillmentStore;
  private final ProductExistenceChecker productExistenceChecker;
  private final StoreExistenceChecker storeExistenceChecker;
  private final WarehouseStore warehouseStore;

  public FulfillmentServiceImpl(
      FulfillmentStore fulfillmentStore,
      ProductExistenceChecker productExistenceChecker,
      StoreExistenceChecker storeExistenceChecker,
      WarehouseStore warehouseStore) {
    this.fulfillmentStore = fulfillmentStore;
    this.productExistenceChecker = productExistenceChecker;
    this.storeExistenceChecker = storeExistenceChecker;
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void assignWarehouseFulfillment(Fulfillment assignment) {
    if (assignment.productId == null
        || assignment.storeId == null
        || assignment.businessUnitCode == null) {
      throw new IllegalArgumentException(
          "productId, storeId and businessUnitCode must all be provided");
    }

    if (!productExistenceChecker.exists(assignment.productId)) {
      throw new IllegalArgumentException(
          "Product with id of " + assignment.productId + " does not exist.");
    }

    if (!storeExistenceChecker.exists(assignment.storeId)) {
      throw new IllegalArgumentException(
          "Store with id of " + assignment.storeId + " does not exist.");
    }

    if (warehouseStore.findByBusinessUnitCode(assignment.businessUnitCode) == null) {
      throw new IllegalArgumentException(
          "Warehouse with business unit code "
              + assignment.businessUnitCode
              + " does not exist.");
    }

    LOGGER.infof(
        "Creating fulfillment: product %d, store %d, warehouse %s",
        assignment.productId, assignment.storeId, assignment.businessUnitCode);

    if (fulfillmentStore.existsExactAssignment(
        assignment.productId, assignment.storeId, assignment.businessUnitCode)) {
      LOGGER.warnf(
          "Rejected fulfillment: product %d is already fulfilled by warehouse %s at store %d",
          assignment.productId, assignment.businessUnitCode, assignment.storeId);
      throw new IllegalArgumentException(
          "This product is already fulfilled by this warehouse at this store.");
    }

    long warehousesForProductAtStore =
        fulfillmentStore.countWarehousesForProductAndStore(
            assignment.productId, assignment.storeId);
    if (warehousesForProductAtStore >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
      LOGGER.warnf(
          "Rejected fulfillment: product %d at store %d already has %d warehouses",
          assignment.productId, assignment.storeId, warehousesForProductAtStore);
      throw new IllegalArgumentException(
          "This product already has the maximum number of warehouses (2) fulfilling it at this"
              + " store.");
    }

    long warehousesForStore =
        fulfillmentStore.countDistinctWarehousesForStore(assignment.storeId);
    if (warehousesForStore >= MAX_WAREHOUSES_PER_STORE) {
      LOGGER.warnf(
          "Rejected fulfillment: store %d already has %d distinct warehouses",
          assignment.storeId, warehousesForStore);
      throw new IllegalArgumentException(
          "This store already has the maximum number of warehouses (3) fulfilling it.");
    }

    long productsForWarehouse =
        fulfillmentStore.countDistinctProductsForWarehouse(assignment.businessUnitCode);
    if (productsForWarehouse >= MAX_PRODUCTS_PER_WAREHOUSE) {
      LOGGER.warnf(
          "Rejected fulfillment: warehouse %s already stocks %d distinct products",
          assignment.businessUnitCode, productsForWarehouse);
      throw new IllegalArgumentException(
          "This warehouse already stocks the maximum number of products (5).");
    }

    fulfillmentStore.create(assignment);

    LOGGER.infof(
        "Created fulfillment: product %d, store %d, warehouse %s",
        assignment.productId, assignment.storeId, assignment.businessUnitCode);
  }
}