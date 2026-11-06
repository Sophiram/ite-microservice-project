package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.platform.product.domain.entity.Product;

public class ProductDomainService {
    public void reserve(Product product, int quantity) {
        product.reserve(quantity);
    }

    public void release(Product product, int quantity) {
        product.release(quantity);
    }
}
