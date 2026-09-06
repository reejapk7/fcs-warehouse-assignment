package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface StoreExistenceChecker {

  boolean exists(Long storeId);
}