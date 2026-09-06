package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  private FakeWarehouseStore warehouseStore;
  private FakeLocationResolver locationResolver;
  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @BeforeEach
  public void setup() {
    warehouseStore = new FakeWarehouseStore();
    locationResolver = new FakeLocationResolver();
    locationResolver.add(new Location("ZWOLLE-001", 1, 40));
    locationResolver.add(new Location("AMSTERDAM-001", 5, 100));

    WarehouseValidator warehouseValidator = new WarehouseValidator(warehouseStore, locationResolver);
    replaceWarehouseUseCase = new ReplaceWarehouseUseCase(warehouseStore, warehouseValidator);
  }

  private static Warehouse newWarehouse(
      String businessUnitCode, String location, Integer capacity, Integer stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }

  @Test
  public void testWhenReplaceValidWarehouseShouldSucceed() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);

    // when
    replaceWarehouseUseCase.replace(newWarehouse);

    // then
    assertNotNull(oldWarehouse.archivedAt);
    assertNotNull(newWarehouse.createdAt);
    assertNull(newWarehouse.archivedAt);
    assertEquals(2, warehouseStore.warehouses.size());
  }

  @Test
  public void testWhenNoActiveWarehouseFoundShouldThrow() {
    // given
    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenExistingWarehouseAlreadyArchivedShouldThrow() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);
    oldWarehouse.archivedAt = java.time.LocalDateTime.now();
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenNewStockNotProvidedShouldThrow() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, null);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenNewStockExceedsNewCapacityShouldThrow() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 5, 10);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenOldWarehouseHasNoRecordedStockShouldThrow() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, null);
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenNewCapacityCannotAccommodateOldStockShouldThrow() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 30);
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 20, 20);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenNewStockDoesNotMatchOldStockShouldThrow() {
    // given
    Warehouse oldWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);
    warehouseStore.create(oldWarehouse);

    Warehouse newWarehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 20);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }

  @Test
  public void testWhenReplacementWouldExceedLocationCapacityShouldThrow() {
    // given: old warehouse uses up almost all of AMSTERDAM-001's capacity (100),
    // another active warehouse also sits at that location
    Warehouse oldWarehouse = newWarehouse("MWH.001", "AMSTERDAM-001", 50, 10);
    warehouseStore.create(oldWarehouse);

    Warehouse otherWarehouse = newWarehouse("MWH.002", "AMSTERDAM-001", 40, 0);
    warehouseStore.create(otherWarehouse);

    // replacing MWH.001 with a much larger capacity that, combined with MWH.002, exceeds 100
    Warehouse newWarehouse = newWarehouse("MWH.001", "AMSTERDAM-001", 70, 10);

    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
  }
}
