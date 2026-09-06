package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreExistenceChecker;
import com.fulfilment.application.monolith.stores.Store;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoreExistenceCheckerImpl implements StoreExistenceChecker {

  @Override
  public boolean exists(Long storeId) {
    return Store.findById(storeId) != null;
  }
}