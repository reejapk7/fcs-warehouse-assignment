package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

  private FakeWarehouseStore warehouseStore;
  private ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @BeforeEach
  public void setup() {
    warehouseStore = new FakeWarehouseStore();
    archiveWarehouseUseCase = new ArchiveWarehouseUseCase(warehouseStore);
  }

  @Test
  public void testWhenArchiveWarehouseShouldSetArchivedAtAndPersist() {
    // given
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 40;
    warehouse.stock = 10;
    warehouseStore.create(warehouse);

    // when
    archiveWarehouseUseCase.archive(warehouse);

    // then
    assertNotNull(warehouse.archivedAt);
    assertEquals(1, warehouseStore.warehouses.size());
  }
}
