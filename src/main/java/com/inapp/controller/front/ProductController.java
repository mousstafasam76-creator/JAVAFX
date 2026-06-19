package com.inapp.controller.front;

import com.inapp.model.Product;
import com.inapp.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductController {
    
    private static ProductController instance;
    private ProductService productService;
    private ObservableList<Product> products;
    
    private ProductController() {
        productService = new ProductService();
        products = FXCollections.observableArrayList();
        loadProducts();
    }
    
    public static ProductController getInstance() {
        if (instance == null) {
            instance = new ProductController();
        }
        return instance;
    }
    
    public void loadProducts() {
        products.setAll(productService.getAllProducts());
    }
    
    public ObservableList<Product> getProducts() {
        return products;
    }
    
    public Product findById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public void addProduct(Product product) {
        productService.addProduct(product);
        loadProducts();
    }
    
    public void updateProduct(Product product) {
        productService.updateProduct(product);
        loadProducts();
    }
    
    public void deleteProduct(Product product) {
        productService.deleteProduct(product);
        loadProducts();
    }
    
    public void refresh() {
        loadProducts();
    }
    
    public ObservableList<String> getCategories() {
        return FXCollections.observableArrayList(productService.getAllCategories());
    }
}