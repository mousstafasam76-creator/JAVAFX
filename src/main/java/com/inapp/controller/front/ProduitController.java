package com.inapp.controller.front;

import com.inapp.model.Product;
import com.inapp.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.List;

public class ProduitController {
    
    private final ProductService productService;
    private final ObservableList<Product> products;
    
    public ProduitController() {
        this.productService = new ProductService();
        this.products = FXCollections.observableArrayList();
        loadProducts();
    }
    
    public ObservableList<Product> getProducts() {
        return products;
    }
    
    public void loadProducts() {
        products.clear();
        List<Product> list = productService.getAllProducts();
        products.addAll(list);
    }
    
    public Product getProductById(int id) {
        return productService.getProductById(id);
    }
    
    public int createProduct(String nomp, int prix, int quantite, String description, int createdBy, int categorieId) {
        Product product = new Product(0, nomp, prix, quantite, description, 
                                      createdBy, LocalDateTime.now(), LocalDateTime.now(), 
                                      categorieId, "");
        int newId = productService.createProduct(product);
        if (newId > 0) {
            product.setId(newId);
            products.add(0, product);
        }
        return newId;
    }
    
    public boolean updateProduct(Product product) {
        boolean success = productService.updateProduct(product);
        if (success) {
            loadProducts();
        }
        return success;
    }
    
    public boolean deleteProduct(int id) {
        boolean success = productService.deleteProduct(id);
        if (success) {
            products.removeIf(p -> p.getId() == id);
        }
        return success;
    }
}