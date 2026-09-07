package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductExistenceChecker;
import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreExistenceChecker;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FulfillmentValidator {

  private static final Logger LOGGER = Logger.getLogger(FulfillmentValidator.class.getName());

  private static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  private static final int MAX_WAREHOUSES_PER_STORE = 3;
  private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

  private final FulfillmentStore fulfillmentStore;
  private final ProductExistenceChecker productExistenceChecker;
  private final StoreExistenceChecker storeExistenceChecker;
  private final WarehouseStore warehouseStore;

  public FulfillmentValidator(
      FulfillmentStore fulfillmentStore,
      ProductExistenceChecker productExistenceChecker,
      StoreExistenceChecker storeExistenceChecker,
      WarehouseStore warehouseStore) {
    this.fulfillmentStore = fulfillmentStore;
    this.productExistenceChecker = productExistenceChecker;
    this.storeExistenceChecker = storeExistenceChecker;
    this.warehouseStore = warehouseStore;
  }

  /** Validates that productId, storeId and businessUnitCode were all provided. */
  public void validateRequiredFields(Fulfillment assignment) {
    if (assignment.productId == null
        || assignment.storeId == null
        || assignment.businessUnitCode == null) {
      throw new IllegalArgumentException(
          "productId, storeId and businessUnitCode must all be provided");
    }
  }

  public void validateProductExists(Long productId) {
    if (!productExistenceChecker.exists(productId)) {
      throw new IllegalArgumentException("Product with id of " + productId + " does not exist.");
    }
  }

  public void validateStoreExists(Long storeId) {
    if (!storeExistenceChecker.exists(storeId)) {
      throw new IllegalArgumentException("Store with id of " + storeId + " does not exist.");
    }
  }

  public void validateWarehouseExists(String businessUnitCode) {
    if (warehouseStore.findByBusinessUnitCode(businessUnitCode) == null) {
      throw new IllegalArgumentException(
          "Warehouse with business unit code " + businessUnitCode + " does not exist.");
    }
  }

  /** Validates that this exact product/store/warehouse combination isn't already assigned. */
  public void validateNotDuplicateAssignment(
      Long productId, Long storeId, String businessUnitCode) {
    if (fulfillmentStore.existsExactAssignment(productId, storeId, businessUnitCode)) {
      LOGGER.warnf(
          "Rejected fulfillment: product %d is already fulfilled by warehouse %s at store %d",
          productId, businessUnitCode, storeId);
      throw new IllegalArgumentException(
          "This product is already fulfilled by this warehouse at this store.");
    }
  }

  /** Validates a product isn't already fulfilled by the max number of warehouses at a store. */
  public void validateWarehouseLimitPerProductAndStore(Long productId, Long storeId) {
    long warehousesForProductAtStore =
        fulfillmentStore.countWarehousesForProductAndStore(productId, storeId);
    if (warehousesForProductAtStore >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
      LOGGER.warnf(
          "Rejected fulfillment: product %d at store %d already has %d warehouses",
          productId, storeId, warehousesForProductAtStore);
      throw new IllegalArgumentException(
          "This product already has the maximum number of warehouses (2) fulfilling it at this"
              + " store.");
    }
  }

  /** Validates a store isn't already fulfilled by the max number of distinct warehouses. */
  public void validateStoreWarehouseLimit(Long storeId) {
    long warehousesForStore = fulfillmentStore.countDistinctWarehousesForStore(storeId);
    if (warehousesForStore >= MAX_WAREHOUSES_PER_STORE) {
      LOGGER.warnf(
          "Rejected fulfillment: store %d already has %d distinct warehouses",
          storeId, warehousesForStore);
      throw new IllegalArgumentException(
          "This store already has the maximum number of warehouses (3) fulfilling it.");
    }
  }

  /** Validates a warehouse doesn't already stock the max number of distinct products. */
  public void validateWarehouseProductLimit(String businessUnitCode) {
    long productsForWarehouse =
        fulfillmentStore.countDistinctProductsForWarehouse(businessUnitCode);
    if (productsForWarehouse >= MAX_PRODUCTS_PER_WAREHOUSE) {
      LOGGER.warnf(
          "Rejected fulfillment: warehouse %s already stocks %d distinct products",
          businessUnitCode, productsForWarehouse);
      throw new IllegalArgumentException(
          "This warehouse already stocks the maximum number of products (5).");
    }
  }
}
