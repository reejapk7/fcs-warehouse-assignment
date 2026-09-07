package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import java.util.HashMap;
import java.util.Map;

/** A simple in-memory fake, used to unit test warehouse business logic without the real gateway. */
public class FakeLocationResolver implements LocationResolver {

  private final Map<String, Location> locations = new HashMap<>();

  public void add(Location location) {
    locations.put(location.identification, location);
  }

  @Override
  public Location resolveByIdentifier(String identifier) {
    if (identifier == null || identifier.isBlank()) {
      throw new IllegalArgumentException("Location identifier must not be null or blank");
    }

    Location location = locations.get(identifier);
    if (location == null) {
      throw new IllegalArgumentException("No location found for identifier: " + identifier);
    }

    return location;
  }
}
