package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;

/** A simple in-memory fake, used to unit test warehouse business logic without a real database. */
public class FakeWarehouseStore implements WarehouseStore {

  public final List<Warehouse> warehouses = new ArrayList<>();

  @Override
  public List<Warehouse> getAll() {
    return warehouses;
  }

  @Override
  public void create(Warehouse warehouse) {
    warehouses.add(warehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    // the use cases mutate the same Warehouse instance in place, nothing extra needed here
  }

  @Override
  public void remove(Warehouse warehouse) {
    warehouses.remove(warehouse);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    return warehouses.stream()
        .filter(warehouse -> warehouse.businessUnitCode.equals(buCode))
        .findFirst()
        .orElse(null);
  }
}
