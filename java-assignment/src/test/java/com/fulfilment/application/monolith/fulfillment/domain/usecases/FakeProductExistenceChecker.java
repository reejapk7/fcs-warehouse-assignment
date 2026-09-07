package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductExistenceChecker;
import java.util.HashSet;
import java.util.Set;

public class FakeProductExistenceChecker implements ProductExistenceChecker {

  private final Set<Long> existingProductIds = new HashSet<>();

  public void add(Long productId) {
    existingProductIds.add(productId);
  }

  @Override
  public boolean exists(Long productId) {
    return existingProductIds.contains(productId);
  }
}
