package com.inapp.dao;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Product;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.nomcat as categorie_nom FROM produits p " +
                     "LEFT JOIN categories c ON p.categorie_id = c.id " +
                     "ORDER BY p.id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll: " + e.getMessage());
        }
        return products;
    }
    
    public Product findById(int id) {
        String sql = "SELECT p.*, c.nomcat as categorie_nom FROM produits p " +
                     "LEFT JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById: " + e.getMessage());
        }
        return null;
    }
    
    public int create(Product product) {
        String sql = "INSERT INTO produits (nomp, prix, quantite, description, created_by, created_at, updated_at, categorie_id) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW(), ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, product.getNomp());
            stmt.setInt(2, product.getPrix());
            stmt.setInt(3, product.getQuantite());
            stmt.setString(4, product.getDescription());
            stmt.setInt(5, product.getCreatedBy());
            stmt.setInt(6, product.getCategorieId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur create: " + e.getMessage());
        }
        return -1;
    }
    
    public boolean update(Product product) {
        String sql = "UPDATE produits SET nomp = ?, prix = ?, quantite = ?, description = ?, " +
                     "categorie_id = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getNomp());
            stmt.setInt(2, product.getPrix());
            stmt.setInt(3, product.getQuantite());
            stmt.setString(4, product.getDescription());
            stmt.setInt(5, product.getCategorieId());
            stmt.setInt(6, product.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur update: " + e.getMessage());
        }
        return false;
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM produits WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur delete: " + e.getMessage());
        }
        return false;
    }
    
    private Product mapProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nomp = rs.getString("nomp");
        int prix = rs.getInt("prix");
        int quantite = rs.getInt("quantite");
        String description = rs.getString("description");
        int createdBy = rs.getInt("created_by");
        LocalDateTime createdAt = rs.getTimestamp("created_at") != null ? 
            rs.getTimestamp("created_at").toLocalDateTime() : null;
        LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null;
        int categorieId = rs.getInt("categorie_id");
        String categorieNom = rs.getString("categorie_nom");
        
        return new Product(id, nomp, prix, quantite, description, 
                          createdBy, createdAt, updatedAt, categorieId, categorieNom);
    }
}