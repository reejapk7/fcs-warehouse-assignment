package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;

public interface FulfillmentService {

  void assignWarehouseFulfillment(Fulfillment assignment);
}