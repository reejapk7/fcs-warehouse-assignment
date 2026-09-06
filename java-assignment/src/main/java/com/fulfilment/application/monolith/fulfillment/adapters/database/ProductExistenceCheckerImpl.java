package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductExistenceChecker;
import com.fulfilment.application.monolith.products.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductExistenceCheckerImpl implements ProductExistenceChecker {

  private final ProductRepository productRepository;

  public ProductExistenceCheckerImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public boolean exists(Long productId) {
    return productRepository.findById(productId) != null;
  }
}