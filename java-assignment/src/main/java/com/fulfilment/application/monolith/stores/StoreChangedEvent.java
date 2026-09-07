package com.fulfilment.application.monolith.stores;

/**
 * Fired by {@link StoreResource} once a {@link Store} change has been persisted, so that
 * downstream systems (e.g. {@link LegacyStoreManagerGateway}) can be notified only after the
 * change is confirmed in the database.
 */
public class StoreChangedEvent {

  public enum ChangeType {
    CREATED,
    UPDATED
  }

  public final Store store;
  public final ChangeType changeType;

  public StoreChangedEvent(Store store, ChangeType changeType) {
    this.store = store;
    this.changeType = changeType;
  }
}
