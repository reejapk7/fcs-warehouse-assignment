package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.FakeWarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FulfillmentServiceImplTest {

  private FakeFulfillmentStore fulfillmentStore;
  private FakeProductExistenceChecker productExistenceChecker;
  private FakeStoreExistenceChecker storeExistenceChecker;
  private FakeWarehouseStore warehouseStore;
  private FulfillmentServiceImpl fulfillmentService;

  @BeforeEach
  public void setup() {
    fulfillmentStore = new FakeFulfillmentStore();
    productExistenceChecker = new FakeProductExistenceChecker();
    storeExistenceChecker = new FakeStoreExistenceChecker();
    warehouseStore = new FakeWarehouseStore();

    FulfillmentValidator fulfillmentValidator =
        new FulfillmentValidator(
            fulfillmentStore, productExistenceChecker, storeExistenceChecker, warehouseStore);
    fulfillmentService = new FulfillmentServiceImpl(fulfillmentStore, fulfillmentValidator);

    productExistenceChecker.add(1L);
    storeExistenceChecker.add(1L);

    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    warehouseStore.create(warehouse);
  }

  private static Fulfillment newAssignment(Long productId, Long storeId, String buCode) {
    Fulfillment assignment = new Fulfillment();
    assignment.productId = productId;
    assignment.storeId = storeId;
    assignment.businessUnitCode = buCode;
    return assignment;
  }

  @Test
  public void testWhenAssignValidFulfillmentShouldSucceed() {
    // given
    Fulfillment assignment = newAssignment(1L, 1L, "MWH.001");

    // when
    fulfillmentService.assignWarehouseFulfillment(assignment);

    // then
    assertEquals(1, fulfillmentStore.assignments.size());
  }

  @Test
  public void testWhenProductIdMissingShouldThrow() {
    // given
    Fulfillment assignment = newAssignment(null, 1L, "MWH.001");

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(assignment));
  }

  @Test
  public void testWhenStoreIdMissingShouldThrow() {
    // given
    Fulfillment assignment = newAssignment(1L, null, "MWH.001");

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(assignment));
  }

  @Test
  public void testWhenBusinessUnitCodeMissingShouldThrow() {
    // given
    Fulfillment assignment = newAssignment(1L, 1L, null);

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(assignment));
  }

  @Test
  public void testWhenProductDoesNotExistShouldThrow() {
    // given
    Fulfillment assignment = newAssignment(999L, 1L, "MWH.001");

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(assignment));
  }

  @Test
  public void testWhenStoreDoesNotExistShouldThrow() {
    // given
    Fulfillment assignment = newAssignment(1L, 999L, "MWH.001");

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(assignment));
  }

  @Test
  public void testWhenWarehouseDoesNotExistShouldThrow() {
    // given
    Fulfillment assignment = newAssignment(1L, 1L, "MWH.999");

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(assignment));
  }

  @Test
  public void testWhenExactAssignmentAlreadyExistsShouldThrow() {
    // given
    fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.001"));

    // when / then
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.001")));
    assertEquals(1, fulfillmentStore.assignments.size());
  }

  @Test
  public void testWhenProductAlreadyHasMaxWarehousesAtStoreShouldThrow() {
    // given: product 1 at store 1 is already fulfilled by 2 different warehouses
    warehouseStore.create(warehouseWithCode("MWH.002"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.001"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.002"));

    warehouseStore.create(warehouseWithCode("MWH.003"));

    // when / then: a third warehouse for the same product+store should be rejected
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.003")));
  }

  @Test
  public void testWhenStoreAlreadyHasMaxDistinctWarehousesShouldThrow() {
    // given: store 1 already fulfilled via 3 distinct warehouses (different products)
    productExistenceChecker.add(2L);
    productExistenceChecker.add(3L);
    warehouseStore.create(warehouseWithCode("MWH.002"));
    warehouseStore.create(warehouseWithCode("MWH.003"));
    warehouseStore.create(warehouseWithCode("MWH.004"));

    fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.001"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(2L, 1L, "MWH.002"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(3L, 1L, "MWH.003"));

    // when / then: a 4th distinct warehouse for the same store should be rejected
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(newAssignment(3L, 1L, "MWH.004")));
  }

  @Test
  public void testWhenWarehouseAlreadyStocksMaxDistinctProductsShouldThrow() {
    // given: warehouse MWH.001 already stocks 5 distinct products (across different stores)
    for (long productId = 2; productId <= 5; productId++) {
      productExistenceChecker.add(productId);
    }
    storeExistenceChecker.add(2L);

    fulfillmentService.assignWarehouseFulfillment(newAssignment(1L, 1L, "MWH.001"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(2L, 1L, "MWH.001"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(3L, 1L, "MWH.001"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(4L, 1L, "MWH.001"));
    fulfillmentService.assignWarehouseFulfillment(newAssignment(5L, 2L, "MWH.001"));

    productExistenceChecker.add(6L);

    // when / then: a 6th distinct product for the same warehouse should be rejected
    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.assignWarehouseFulfillment(newAssignment(6L, 2L, "MWH.001")));
  }

  private static Warehouse warehouseWithCode(String businessUnitCode) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    return warehouse;
  }
}
