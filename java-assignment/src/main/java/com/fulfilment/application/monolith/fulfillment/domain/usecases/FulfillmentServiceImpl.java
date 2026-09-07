package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentService;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FulfillmentServiceImpl implements FulfillmentService {

  private static final Logger LOGGER = Logger.getLogger(FulfillmentServiceImpl.class.getName());

  private final FulfillmentStore fulfillmentStore;
  private final FulfillmentValidator fulfillmentValidator;

  public FulfillmentServiceImpl(
      FulfillmentStore fulfillmentStore, FulfillmentValidator fulfillmentValidator) {
    this.fulfillmentStore = fulfillmentStore;
    this.fulfillmentValidator = fulfillmentValidator;
  }

  @Override
  public void assignWarehouseFulfillment(Fulfillment assignment) {
    fulfillmentValidator.validateRequiredFields(assignment);
    fulfillmentValidator.validateProductExists(assignment.productId);
    fulfillmentValidator.validateStoreExists(assignment.storeId);
    fulfillmentValidator.validateWarehouseExists(assignment.businessUnitCode);

    LOGGER.infof(
        "Creating fulfillment: product %d, store %d, warehouse %s",
        assignment.productId, assignment.storeId, assignment.businessUnitCode);

    fulfillmentValidator.validateNotDuplicateAssignment(
        assignment.productId, assignment.storeId, assignment.businessUnitCode);
    fulfillmentValidator.validateWarehouseLimitPerProductAndStore(
        assignment.productId, assignment.storeId);
    fulfillmentValidator.validateStoreWarehouseLimit(assignment.storeId);
    fulfillmentValidator.validateWarehouseProductLimit(assignment.businessUnitCode);

    fulfillmentStore.create(assignment);

    LOGGER.infof(
        "Created fulfillment: product %d, store %d, warehouse %s",
        assignment.productId, assignment.storeId, assignment.businessUnitCode);
  }
}