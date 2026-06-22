package com.inapp.controller.front;

import com.inapp.model.Category;
import com.inapp.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CategoryController {
    
    private static CategoryController instance;
    private CategoryService categoryService;
    private ObservableList<Category> categories;
    
    private CategoryController() {
        categoryService = new CategoryService();
        categories = FXCollections.observableArrayList();
        loadCategories();
    }
    
    public static CategoryController getInstance() {
        if (instance == null) {
            System.out.println("🔧 Création de l'instance CategoryController");
            instance = new CategoryController();
        }
        return instance;
    }
    
    public void loadCategories() {
        System.out.println("🔄 Chargement des catégories...");
        try {
            java.util.List<Category> list = categoryService.getAllCategories();
            System.out.println("📊 Catégories reçues: " + (list != null ? list.size() : 0));
            
            categories.clear();
            if (list != null && !list.isEmpty()) {
                categories.addAll(list);
                System.out.println("✅ " + categories.size() + " catégories chargées");
            } else {
                System.out.println("⚠️ Aucune catégorie trouvée dans la base");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public ObservableList<Category> getCategories() {
        if (categories == null || categories.isEmpty()) {
            System.out.println("🔄 Rechargement automatique des catégories");
            loadCategories();
        }
        return categories;
    }
    
    public Category findById(int id) {
        if (categories == null || categories.isEmpty()) {
            loadCategories();
        }
        return categories.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public void addCategory(Category category) {
        System.out.println("➕ Ajout d'une catégorie: " + category.getName());
        categoryService.addCategory(category);
        loadCategories();
    }
    
    public void updateCategory(Category category) {
        System.out.println("✏️ Mise à jour de la catégorie: " + category.getName());
        categoryService.updateCategory(category);
        loadCategories();
    }
    
    public void deleteCategory(int id) {
        System.out.println("🗑️ Suppression de la catégorie ID: " + id);
        categoryService.deleteCategory(id);
        loadCategories();
    }
    
    public void approveCategory(int id, int approvedBy) {
        System.out.println("✅ Approbation de la catégorie ID: " + id);
        categoryService.approveCategory(id, approvedBy);
        loadCategories();
    }
    
    public void rejectCategory(int id, String reason) {
        System.out.println("❌ Rejet de la catégorie ID: " + id);
        categoryService.rejectCategory(id, reason);
        loadCategories();
    }
    
    public int getProductCount(int categoryId) {
        return categoryService.getProductCountByCategory(categoryId);
    }
    
    // ========== MÉTHODE refresh AJOUTÉE ==========
    public void refresh() {
        System.out.println("🔄 Rafraîchissement manuel des catégories");
        loadCategories();
    }
    
    public boolean isLoaded() {
        return categories != null && !categories.isEmpty();
    }
}