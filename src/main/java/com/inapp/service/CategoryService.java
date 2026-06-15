package com.inapp.service;

import com.inapp.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CategoryService {

    private static CategoryService instance;
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private int nextId = 1;

    private CategoryService() {
        // Données de test
        categories.add(new Category(nextId++, "Lave-linge", "Machines à laver le linge", "approved"));
        categories.add(new Category(nextId++, "Réfrigérateur", "Frigos et congélateurs", "approved"));
        categories.add(new Category(nextId++, "Cuisinière", "Cuisinières à gaz et électriques", "pending"));
        categories.add(new Category(nextId++, "Climatiseur", "Climatiseurs et ventilateurs", "rejected"));
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public void addCategory(String name, String description, String status, String imageUrl) {
        Category cat = new Category(nextId++, name, description, status);
        cat.setImageUrl(imageUrl);
        categories.add(cat);
    }

    public void updateCategory(Category category, String name, String description, String status, String imageUrl) {
        category.setName(name);
        category.setDescription(description);
        category.setStatus(status);
        category.setImageUrl(imageUrl);
    }

    public void deleteCategory(Category category) {
        categories.remove(category);
    }

    public String statusLabel(String status) {
        if (status == null) return "En attente";
        switch (status) {
            case "approved": return "Approuvée";
            case "rejected": return "Rejetée";
            default: return "En attente";
        }
    }

    public String statusValue(String label) {
        if (label == null) return "pending";
        switch (label) {
            case "Approuvée": return "approved";
            case "Rejetée": return "rejected";
            default: return "pending";
        }
    }

    public ObservableList<Category> getPendingCategories() {
        ObservableList<Category> pending = FXCollections.observableArrayList();
        for (Category c : categories) {
            if ("pending".equals(c.getStatus())) {
                pending.add(c);
            }
        }
        return pending;
    }
}