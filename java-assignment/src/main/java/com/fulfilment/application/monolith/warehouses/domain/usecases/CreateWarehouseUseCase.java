package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(CreateWarehouseUseCase.class.getName());

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void create(Warehouse warehouse) {
    LOGGER.infof("Creating warehouse with business unit code %s", warehouse.businessUnitCode);

    warehouseValidator.validateBusinessUnitCodeIsAvailable(warehouse.businessUnitCode);

    warehouseValidator.validateLocationAndCapacity(warehouse, null);

    if (warehouse.stock != null && warehouse.stock > warehouse.capacity) {
      LOGGER.warnf(
          "Rejected create: stock %d exceeds capacity %d", warehouse.stock, warehouse.capacity);
      throw new IllegalArgumentException("Warehouse stock cannot exceed its own capacity");
    }

    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;

    warehouseStore.create(warehouse);

    LOGGER.infof("Created warehouse with business unit code %s", warehouse.businessUnitCode);
  }
}