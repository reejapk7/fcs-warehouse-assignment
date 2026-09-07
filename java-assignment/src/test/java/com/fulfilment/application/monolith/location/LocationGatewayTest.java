package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

public class LocationGatewayTest {

  private final LocationGateway locationGateway = new LocationGateway();

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    // (LocationGateway comes pre-loaded with a fixed set of locations)

    // when
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test
  public void testWhenResolveAnotherExistingLocationShouldReturnItsOwnValues() {
    // when
    Location location = locationGateway.resolveByIdentifier("AMSTERDAM-001");

    // then
    assertEquals("AMSTERDAM-001", location.identification);
    assertEquals(5, location.maxNumberOfWarehouses);
    assertEquals(100, location.maxCapacity);
  }

  @Test
  public void testWhenResolveUnknownLocationShouldThrow() {
    // when / then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> locationGateway.resolveByIdentifier("DOES-NOT-EXIST"));

    assertEquals("No location found for identifier: DOES-NOT-EXIST", exception.getMessage());
  }

  @Test
  public void testWhenResolveNullIdentifierShouldThrow() {
    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> locationGateway.resolveByIdentifier(null));
  }

  @Test
  public void testWhenResolveBlankIdentifierShouldThrow() {
    // when / then
    assertThrows(
        IllegalArgumentException.class, () -> locationGateway.resolveByIdentifier("   "));
  }
}
