package com.inapp.service;

import com.inapp.dao.ProductDAO;
import com.inapp.model.Product;
import java.util.List;

public class ProductService {
    
    private final ProductDAO productDAO;
    
    public ProductService() {
        this.productDAO = new ProductDAO();
    }
    
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    public Product getProductById(int id) {
        return productDAO.findById(id);
    }
    
    public int createProduct(Product product) {
        return productDAO.create(product);
    }
    
    public boolean updateProduct(Product product) {
        return productDAO.update(product);
    }
    
    public boolean deleteProduct(int id) {
        return productDAO.delete(id);
    }
}