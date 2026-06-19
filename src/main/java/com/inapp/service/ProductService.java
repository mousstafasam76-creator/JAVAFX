package com.inapp.service;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, nomp, prix, quantite, description, image, created_by, created_at, categorie_id FROM produits ORDER BY id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("nomp"));
                product.setPrice(rs.getDouble("prix"));
                product.setQuantity(rs.getInt("quantite"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image"));
                product.setCreator(rs.getString("created_by"));
                product.setDate(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : "");
                product.setCategoryName(getCategoryName(rs.getInt("categorie_id")));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    private String getCategoryName(int categoryId) {
        if (categoryId <= 0) return "Non catégorisé";
        String sql = "SELECT nomcat FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nomcat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Non catégorisé";
    }
    
    public Product getProductById(int id) {
        String sql = "SELECT id, nomp, prix, quantite, description, image, created_by, created_at, categorie_id FROM produits WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("nomp"));
                product.setPrice(rs.getDouble("prix"));
                product.setQuantity(rs.getInt("quantite"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image"));
                product.setCreator(rs.getString("created_by"));
                product.setDate(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : "");
                product.setCategoryName(getCategoryName(rs.getInt("categorie_id")));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addProduct(Product product) {
        String sql = "INSERT INTO produits (nomp, prix, quantite, description, image, created_by, categorie_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getImageUrl());
            ps.setInt(6, 1); // ID utilisateur (à adapter)
            ps.setInt(7, getCategoryId(product.getCategoryName()));
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateProduct(Product product) {
        String sql = "UPDATE produits SET nomp = ?, prix = ?, quantite = ?, description = ?, image = ?, categorie_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getImageUrl());
            ps.setInt(6, getCategoryId(product.getCategoryName()));
            ps.setInt(7, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteProduct(int id) {
        String sql = "DELETE FROM produits WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteProduct(Product product) {
        deleteProduct(product.getId());
    }
    
    private int getCategoryId(String categoryName) {
        if (categoryName == null || categoryName.isEmpty() || categoryName.equals("Non catégorisé")) {
            return 0;
        }
        String sql = "SELECT id FROM categories WHERE nomcat = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT nomcat FROM categories WHERE status = 'approved' ORDER BY nomcat";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("nomcat"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}