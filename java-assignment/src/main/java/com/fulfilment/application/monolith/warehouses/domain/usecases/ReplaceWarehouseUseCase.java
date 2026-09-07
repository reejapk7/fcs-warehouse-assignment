package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(ReplaceWarehouseUseCase.class.getName());

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    LOGGER.infof(
        "Replacing warehouse with business unit code %s", newWarehouse.businessUnitCode);

    Warehouse oldWarehouse =
        warehouseValidator.validateActiveWarehouseExists(newWarehouse.businessUnitCode);

    warehouseValidator.validateLocationAndCapacity(newWarehouse, oldWarehouse.businessUnitCode);

    if (newWarehouse.stock == null) {
      LOGGER.warnf(
          "Rejected replace: stock not provided for business unit code %s",
          newWarehouse.businessUnitCode);
      throw new IllegalArgumentException("Warehouse stock must be provided");
    }

    if (newWarehouse.stock > newWarehouse.capacity) {
      LOGGER.warnf(
          "Rejected replace: stock %d exceeds capacity %d",
          newWarehouse.stock, newWarehouse.capacity);
      throw new IllegalArgumentException("Warehouse stock cannot exceed its own capacity");
    }

    if (oldWarehouse.stock == null) {
      LOGGER.warnf(
          "Rejected replace: old warehouse %s has no recorded stock",
          oldWarehouse.businessUnitCode);
      throw new IllegalArgumentException(
          "The warehouse being replaced has no recorded stock; cannot validate replacement");
    }

    if (newWarehouse.capacity < oldWarehouse.stock) {
      LOGGER.warnf(
          "Rejected replace: new capacity %d cannot accommodate old stock %d",
          newWarehouse.capacity, oldWarehouse.stock);
      throw new IllegalArgumentException(
          "New warehouse capacity must be able to accommodate the stock of the warehouse being"
              + " replaced");
    }

    if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
      LOGGER.warnf(
          "Rejected replace: new stock %d does not match old stock %d",
          newWarehouse.stock, oldWarehouse.stock);
      throw new IllegalArgumentException(
          "New warehouse stock must match the stock of the warehouse being replaced");
    }

    oldWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);

    LOGGER.infof(
        "Replaced warehouse with business unit code %s", newWarehouse.businessUnitCode);
  }
}