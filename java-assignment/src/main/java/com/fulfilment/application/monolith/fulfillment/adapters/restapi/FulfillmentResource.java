package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentService;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

  @Inject private FulfillmentStore fulfillmentStore;
  @Inject private FulfillmentService fulfillmentOperation;

  @GET
  public List<FulfillmentResponse> get() {
    return fulfillmentStore.getAll().stream().map(this::toResponse).toList();
  }

  @POST
  @Transactional
  public Response create(FulfillmentRequest request) {
    Fulfillment assignment = toDomain(request);

    try {
      fulfillmentOperation.assignWarehouseFulfillment(assignment);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 400);
    }

    return Response.ok(toResponse(assignment)).status(201).build();
  }

  private Fulfillment toDomain(FulfillmentRequest request) {
    Fulfillment assignment = new Fulfillment();
    assignment.productId = request.productId;
    assignment.storeId = request.storeId;
    assignment.businessUnitCode = request.businessUnitCode;

    return assignment;
  }

  private FulfillmentResponse toResponse(Fulfillment assignment) {
    FulfillmentResponse response = new FulfillmentResponse();
    response.id = assignment.id;
    response.productId = assignment.productId;
    response.storeId = assignment.storeId;
    response.businessUnitCode = assignment.businessUnitCode;

    return response;
  }
}