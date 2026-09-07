package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "product_store_warehouse",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"productId", "storeId", "businessUnitCode"}))
public class DbProductStoreWarehouse extends PanacheEntity {

  @Column(nullable = false)
  public Long productId;

  @Column(nullable = false)
  public Long storeId;

  @Column(nullable = false)
  public String businessUnitCode;

  @ManyToOne
  @JoinColumn(name = "productId", insertable = false, updatable = false)
  public Product product;

  @ManyToOne
  @JoinColumn(name = "storeId", insertable = false, updatable = false)
  public Store store;

  @ManyToOne
  @JoinColumn(
      name = "businessUnitCode",
      referencedColumnName = "businessUnitCode",
      insertable = false,
      updatable = false)
  public DbWarehouse warehouse;

  public Fulfillment toFulfillment() {
    Fulfillment fulfillment = new Fulfillment();
    fulfillment.id = this.id;
    fulfillment.productId = this.productId;
    fulfillment.storeId = this.storeId;
    fulfillment.businessUnitCode = this.businessUnitCode;

    return fulfillment;
  }
}