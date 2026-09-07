package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreExistenceChecker;
import java.util.HashSet;
import java.util.Set;

public class FakeStoreExistenceChecker implements StoreExistenceChecker {

  private final Set<Long> existingStoreIds = new HashSet<>();

  public void add(Long storeId) {
    existingStoreIds.add(storeId);
  }

  @Override
  public boolean exists(Long storeId) {
    return existingStoreIds.contains(storeId);
  }
}
