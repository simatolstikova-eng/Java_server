package com.example.storage;

import com.example.model.Product;

public class ProductStorage extends Storage<Product> {
    public ProductStorage() {
        super("data/products.json");
    }

    @Override
    protected String getId(Product product) {
        return product.getId();
    }
}