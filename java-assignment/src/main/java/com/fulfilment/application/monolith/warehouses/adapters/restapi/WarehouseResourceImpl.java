package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.
        CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.jboss.logging.Logger;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class.getName());

  @Inject private WarehouseRepository warehouseRepository;
  @Inject private CreateWarehouseOperation createWarehouseOperation;
  @Inject private ReplaceWarehouseOperation replaceWarehouseOperation;
  @Inject private ArchiveWarehouseOperation archiveWarehouseOperation;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    LOGGER.info("Listing all warehouse units");

    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    LOGGER.infof("Received request to create warehouse %s", data.getBusinessUnitCode());

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
        toDomainWarehouse(data);

    try {
      createWarehouseOperation.create(warehouse);
    } catch (IllegalArgumentException e) {
      LOGGER.warnf("Create warehouse request rejected: %s", e.getMessage());
      throw new WebApplicationException(e.getMessage(), 400);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    LOGGER.infof("Fetching warehouse unit %s", id);

    return toWarehouseResponse(findWarehouseOrThrow(id));
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    LOGGER.infof("Received request to archive warehouse unit %s", id);

    archiveWarehouseOperation.archive(findWarehouseOrThrow(id));
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse findWarehouseOrThrow(
      String id) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
        warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null) {
      LOGGER.warnf("Warehouse unit not found for id: %s", id);
      throw new WebApplicationException("Warehouse unit not found for id: " + id, 404);
    }

    return warehouse;
  }

  @Override
  @Transactional
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    LOGGER.infof("Received request to replace warehouse %s", businessUnitCode);

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse newWarehouse =
        toDomainWarehouse(data);
    newWarehouse.businessUnitCode = businessUnitCode;

    try {
      replaceWarehouseOperation.replace(newWarehouse);
    } catch (IllegalArgumentException e) {
      LOGGER.warnf("Replace warehouse request rejected: %s", e.getMessage());
      throw new WebApplicationException(e.getMessage(), 400);
    }

    return toWarehouseResponse(newWarehouse);
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainWarehouse(
      Warehouse data) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();

    return warehouse;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
