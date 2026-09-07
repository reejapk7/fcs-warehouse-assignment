package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Observes {@link StoreChangedEvent} and propagates the change to the legacy system. Using {@link
 * TransactionPhase#AFTER_SUCCESS} guarantees this only runs once the transaction that fired the
 * event has actually committed, so the legacy system only ever receives confirmed data.
 */
@ApplicationScoped
public class StoreLegacySyncObserver {

  private static final Logger LOGGER = Logger.getLogger(StoreLegacySyncObserver.class.getName());

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;

  public void onStoreChanged(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreChangedEvent event) {
    LOGGER.infof(
        "Syncing store '%s' to legacy system after commit (%s)",
        event.store.name, event.changeType);

    if (event.changeType == StoreChangedEvent.ChangeType.CREATED) {
      legacyStoreManagerGateway.createStoreOnLegacySystem(event.store);
    } else {
      legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store);
    }
  }
}
