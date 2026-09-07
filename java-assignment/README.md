# Java Code Assignment

A Quarkus-based REST API for managing Warehouses, Stores, Products, and their Fulfillment relationships, backed by PostgreSQL. Built as part of a Java coding assessment.

## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](CODE_ASSIGNMENT.md) file

## Quick Start

### 1. Start PostgreSQL

```sh
docker run --name quarkus_test \
  -e POSTGRES_USER=quarkus_test \
  -e POSTGRES_PASSWORD=quarkus_test \
  -e POSTGRES_DB=quarkus_test \
  -p 15432:5432 \
  -d postgres:13.3
```

### 2. Run tests

```sh
./mvnw test
```

### 3. Start the application

```sh
./mvnw quarkus:dev
```

### 4. Explore the API

```sh
curl -X GET http://localhost:8080/warehouse
```

See [API overview](#api-overview) below for the full list of endpoints.

The rest of this README provides the detailed explanation.

## Technology Stack

- Java 17
- Quarkus 3.13.3
- RESTEasy  (Jakarta REST)
- Arc, CDI framework
- Hibernate ORM with Panache
- PostgreSQL
- Maven
- JUnit 5 / REST Assured
- Mockito
- JaCoCo
- OpenAPI Generator (contract-first REST for Warehouse)

## About the code base

This is based on https://github.com/quarkusio/quarkus-quickstarts

### Requirements

To compile and run this demo you will need:

- JDK 17+

In addition, you will need either a PostgreSQL database, or Docker to run one.

### Configuring JDK 17+

Make sure that `JAVA_HOME` environment variables has been set, and that a JDK 17+ `java` command is on the path.

## Building the demo

Execute the Maven build on the root of the project:

```sh
./mvnw package
```

## Running the demo

### Live coding with Quarkus

The Maven Quarkus plugin provides a development mode that supports
live coding. To try this out:

```sh
./mvnw quarkus:dev
```

In this mode you can make changes to the code and have the changes immediately applied, by just refreshing your browser.

    Hot reload works even when modifying your JPA entities.
    Try it! Even the database schema will be updated on the fly.

## (Optional) Run Quarkus in JVM mode

When you're done iterating in developer mode, you can run the application as a conventional jar file.

First compile it:

```sh
./mvnw package
```

Next we need to make sure you have a PostgreSQL instance running (Quarkus automatically starts one for dev and test mode). To set up a PostgreSQL database with Docker:

```sh
docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 15432:5432 postgres:13.3
```

Connection properties for the Agroal datasource are defined in the standard Quarkus configuration file,
`src/main/resources/application.properties`.

Then run it:

```sh
java -jar ./target/quarkus-app/quarkus-run.jar
```

## Web UI

A basic Product-management page (inherited from the original Quarkus quickstart this project is
based on) is served at:

<http://localhost:8080/index.html>

It only covers `Product` create/edit/delete — Store, Warehouse, and Fulfillment have no UI and
are only reachable via the REST API below.

## API Documentation

Warehouse's REST contract is generated from its OpenAPI spec file:

```
src/main/resources/openapi/warehouse-openapi.yaml
```

Store, Product, and Fulfillment are code-first (no generated spec) — see the endpoint table below.

## API overview

| Resource      | Base path      |
|---------------|-----------------|
| Products      | `/product`      |
| Stores        | `/store`        |
| Warehouses    | `/warehouse`    |
| Fulfillment   | `/fulfillment`  |

Example:

```sh
curl -X GET http://localhost:8080/warehouse
```

## Architecture notes

The codebase is organized by feature (`warehouses`, `stores`, `products`, `fulfillment`). Key architectural and design patterns used throughout are described below.

### API design: contract-first vs. code-first

- **Warehouse** — contract-first: `warehouse-openapi.yaml` is the source of truth;
  `quarkus-openapi-generator-server` generates the `WarehouseResource` interface and DTO beans
  from it at build time. `WarehouseResourceImpl` implements that generated interface.
- **Store, Product, Fulfillment** — code-first: hand-written JAX-RS resources, no generated
  contract.

### Data access

- **Store — Active Record**: `Store` extends `PanacheEntity` and persists/queries itself
  (`store.persist()`, `Store.findById(id)`).
- **Product — Repository**: `Product` is a plain `@Entity`, accessed only through
  `ProductRepository implements PanacheRepository<Product>`.
- **Warehouse — Repository + Data Mapper**: `WarehouseRepository` persists `DbWarehouse`
  entities and maps them to the domain `Warehouse` model. This keeps the domain layer
  independent of JPA/Hibernate.
- **Fulfillment — Repository + Data Mapper**: same approach — `ProductStoreWarehouseRepository`
  persists `DbProductStoreWarehouse` entities and maps them to the domain `Fulfillment` model.

### Other patterns in use

- **Dependency Injection / Inversion of Control** — CDI throughout; use cases depend on port
  interfaces, not concrete adapters, which is what makes swapping in fakes/mocks in tests
  possible.
- **Observer** — `StoreResource` fires a `StoreChangedEvent`, observed by
  `StoreLegacySyncObserver` via `@Observes(during = TransactionPhase.AFTER_SUCCESS)`, so the
  legacy system is only notified after the change is confirmed committed to the database.
- **Extracted Validator objects** — business rule checks for `Warehouse` and `Fulfillment` live
  in dedicated `WarehouseValidator` / `FulfillmentValidator` classes, separate from the
  use-case/service classes that orchestrate the actual operations.
- **DTO + Mapper** — REST resources convert between wire-format DTOs and domain models via
  explicit `toDomain(...)`/`toResponse(...)` mapper methods.
- **Exception Translator** — `@Provider ExceptionMapper<Exception>` classes, plus
  `try { ... } catch (IllegalArgumentException e) { throw new WebApplicationException(...) }`
  in the REST layer, translate domain validation failures into HTTP status codes.

## Key Design Decisions

- Warehouse uses an API-first approach with OpenAPI-generated REST contracts.
- Store uses the Panache Active Record pattern; Product and Fulfillment use the Panache
  Repository pattern.
- Warehouse and Fulfillment separate their domain models from their persistence models using a
  Data Mapper.
- Business validation is extracted into dedicated validator classes.
- Store changes are propagated to the legacy system through a CDI event, observed only after
  successful transaction completion.
- REST DTOs are mapped explicitly to domain models to avoid coupling the domain layer to the API
  representation.

## Testing

Run the test suite:

```sh
./mvnw test
```

Run the full Maven verification, including the configured JaCoCo coverage check:

```sh
./mvnw verify
```

The build fails if code coverage is below 80%. The HTML coverage report is generated at
`target/site/jacoco/index.html`.

## Troubleshooting

Using **IntelliJ**, in case the generated code is not recognized and you have compilation failures, you may need to add `target/.../jaxrs` folder as "generated sources".