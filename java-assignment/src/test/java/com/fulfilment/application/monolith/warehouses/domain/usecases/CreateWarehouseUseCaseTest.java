package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private FakeWarehouseStore warehouseStore;
  private FakeLocationResolver locationResolver;
  private CreateWarehouseUseCase createWarehouseUseCase;

  @BeforeEach
  public void setup() {
    warehouseStore = new FakeWarehouseStore();
    locationResolver = new FakeLocationResolver();
    locationResolver.add(new Location("ZWOLLE-001", 1, 40));
    locationResolver.add(new Location("AMSTERDAM-001", 5, 100));

    WarehouseValidator warehouseValidator = new WarehouseValidator(warehouseStore, locationResolver);
    createWarehouseUseCase = new CreateWarehouseUseCase(warehouseStore, warehouseValidator);
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
  public void testWhenCreateValidWarehouseShouldSucceed() {
    // given
    Warehouse warehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, 10);

    // when
    createWarehouseUseCase.create(warehouse);

    // then
    assertEquals(1, warehouseStore.warehouses.size());
    assertNotNull(warehouse.createdAt);
    assertNull(warehouse.archivedAt);
  }

  @Test
  public void testWhenCreateWithoutStockShouldSucceed() {
    // given
    Warehouse warehouse = newWarehouse("MWH.001", "ZWOLLE-001", 40, null);

    // when
    createWarehouseUseCase.create(warehouse);

    // then
    assertEquals(1, warehouseStore.warehouses.size());
  }

  @Test
  public void testWhenBusinessUnitCodeAlreadyExistsShouldThrow() {
    // given
    Warehouse existing = newWarehouse("MWH.001", "AMSTERDAM-001", 50, 0);
    warehouseStore.create(existing);

    Warehouse duplicate = newWarehouse("MWH.001", "ZWOLLE-001", 40, 0);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(duplicate));
    assertEquals(1, warehouseStore.warehouses.size());
  }

  @Test
  public void testWhenLocationAtMaxWarehouseCountShouldThrow() {
    // given
    Warehouse existing = newWarehouse("MWH.001", "ZWOLLE-001", 40, 0);
    warehouseStore.create(existing);

    Warehouse newOne = newWarehouse("MWH.002", "ZWOLLE-001", 10, 0);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(newOne));
    assertEquals(1, warehouseStore.warehouses.size());
  }

  @Test
  public void testWhenLocationCapacityExceededShouldThrow() {
    // given
    Warehouse existing = newWarehouse("MWH.001", "AMSTERDAM-001", 60, 0);
    warehouseStore.create(existing);

    Warehouse newOne = newWarehouse("MWH.002", "AMSTERDAM-001", 41, 0);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(newOne));
  }

  @Test
  public void testWhenCapacityNotProvidedShouldThrow() {
    // given
    Warehouse warehouse = newWarehouse("MWH.001", "ZWOLLE-001", null, 0);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
  }

  @Test
  public void testWhenStockExceedsCapacityShouldThrow() {
    // given
    Warehouse warehouse = newWarehouse("MWH.001", "ZWOLLE-001", 10, 20);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
    assertEquals(0, warehouseStore.warehouses.size());
  }

  @Test
  public void testWhenUnknownLocationShouldThrow() {
    // given
    Warehouse warehouse = newWarehouse("MWH.001", "NOWHERE-001", 10, 0);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
  }

  @Test
  public void testWhenArchivedWarehouseAtLocationShouldNotCountTowardsLimit() {
    // given
    Warehouse archived = newWarehouse("MWH.001", "ZWOLLE-001", 40, 0);
    archived.archivedAt = java.time.LocalDateTime.now();
    warehouseStore.create(archived);

    Warehouse newOne = newWarehouse("MWH.002", "ZWOLLE-001", 40, 0);

    // when
    createWarehouseUseCase.create(newOne);

    // then
    assertEquals(2, warehouseStore.warehouses.size());
  }
}
