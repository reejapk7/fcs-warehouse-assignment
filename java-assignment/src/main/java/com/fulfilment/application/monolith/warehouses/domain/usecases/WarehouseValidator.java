package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WarehouseValidator {

  private static final Logger LOGGER = Logger.getLogger(WarehouseValidator.class.getName());

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public WarehouseValidator(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  /**
   * Resolves the warehouse's location and validates that it can accommodate the warehouse, both
   * in terms of the location's maximum warehouse count and its total capacity.
   *
   * @param excludeBusinessUnitCode a business unit code to exclude from the active-warehouse
   *     count/capacity sum (used by replace, to exclude the warehouse being replaced); pass null
   *     to exclude nothing (used by create).
   */
  public void validateLocationAndCapacity(Warehouse warehouse, String excludeBusinessUnitCode) {
    Location location = locationResolver.resolveByIdentifier(warehouse.location);

    List<Warehouse> activeWarehousesAtLocation =
        warehouseStore.getAll().stream()
            .filter(existing -> Objects.equals(existing.location, warehouse.location))
            .filter(existing -> existing.archivedAt == null)
            .filter(existing -> !Objects.equals(existing.businessUnitCode, excludeBusinessUnitCode))
            .toList();

    if (activeWarehousesAtLocation.size() >= location.maxNumberOfWarehouses) {
      LOGGER.warnf(
          "Rejected: location %s already at max warehouse count (%d)",
          warehouse.location, location.maxNumberOfWarehouses);
      throw new IllegalArgumentException(
          "Location "
              + warehouse.location
              + " has already reached its maximum number of warehouses");
    }

    int usedCapacity =
        activeWarehousesAtLocation.stream()
            .filter(existing -> existing.capacity != null)
            .mapToInt(existing -> existing.capacity)
            .sum();

    if (warehouse.capacity == null) {
      LOGGER.warnf(
          "Rejected: capacity not provided for business unit code %s",
          warehouse.businessUnitCode);
      throw new IllegalArgumentException("Warehouse capacity must be provided");
    }

    if (usedCapacity + warehouse.capacity > location.maxCapacity) {
      LOGGER.warnf(
          "Rejected: capacity %d plus used %d exceeds max %d at location %s",
          warehouse.capacity, usedCapacity, location.maxCapacity, warehouse.location);
      throw new IllegalArgumentException(
          "Warehouse capacity exceeds the maximum capacity available at location "
              + warehouse.location);
    }

  }

  /**
   * Validates that no warehouse currently uses the given business unit code. Used by create,
   * where the code must be brand new.
   */
  public void validateBusinessUnitCodeIsAvailable(String businessUnitCode) {
    if (warehouseStore.findByBusinessUnitCode(businessUnitCode) != null) {
      LOGGER.warnf("Rejected: business unit code %s already exists", businessUnitCode);
      throw new IllegalArgumentException(
          "A warehouse with business unit code " + businessUnitCode + " already exists");
    }
  }

  /**
   * Validates that an active (non-archived) warehouse exists for the given business unit code,
   * and returns it. Used by replace, where the warehouse being replaced must already exist.
   */
  public Warehouse validateActiveWarehouseExists(String businessUnitCode) {
    Warehouse warehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);

    if (warehouse == null || warehouse.archivedAt != null) {
      LOGGER.warnf(
          "Rejected: no active warehouse found for business unit code %s", businessUnitCode);
      throw new IllegalArgumentException(
          "No active warehouse found for business unit code: " + businessUnitCode);
    }

    return warehouse;
  }
}