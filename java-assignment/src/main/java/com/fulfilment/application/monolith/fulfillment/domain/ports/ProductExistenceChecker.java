package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface ProductExistenceChecker {

  boolean exists(Long productId);
}