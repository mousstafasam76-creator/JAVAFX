package com.inapp.service;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY nomcat";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("nomcat"));
                cat.setStatus(rs.getString("status") != null ? rs.getString("status") : "pending");
                cat.setImageUrl(rs.getString("image"));
                cat.setCreatedBy(rs.getInt("created_by"));
                cat.setApprovedBy(rs.getInt("approved_by"));
                cat.setApprovedAt(rs.getString("approved_at"));
                cat.setRejectionReason(rs.getString("rejection_reason"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("nomcat"));
                cat.setStatus(rs.getString("status") != null ? rs.getString("status") : "pending");
                cat.setImageUrl(rs.getString("image"));
                cat.setCreatedBy(rs.getInt("created_by"));
                cat.setApprovedBy(rs.getInt("approved_by"));
                cat.setApprovedAt(rs.getString("approved_at"));
                cat.setRejectionReason(rs.getString("rejection_reason"));
                return cat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addCategory(Category category) {
        String sql = "INSERT INTO categories (nomcat, status, image, created_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getStatus() != null ? category.getStatus() : "pending");
            ps.setString(3, category.getImageUrl());
            ps.setInt(4, category.getCreatedBy() > 0 ? category.getCreatedBy() : 1);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCategory(Category category) {
        String sql = "UPDATE categories SET nomcat = ?, status = ?, image = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getStatus());
            ps.setString(3, category.getImageUrl());
            ps.setInt(4, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void approveCategory(int id, int approvedBy) {
        String sql = "UPDATE categories SET status = 'approved', approved_by = ?, approved_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, approvedBy);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejectCategory(int id, String reason) {
        String sql = "UPDATE categories SET status = 'rejected', rejection_reason = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getProductCountByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM produits WHERE categorie_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}