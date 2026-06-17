package com.inapp.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.inapp.view.components.Sidebar;
import com.inapp.view.components.Footer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NavigationManager {
    
    private Stage primaryStage;
    private Map<String, Parent> views;
    private Scene currentScene;
    private Stack<String> history;
    private StackPane rootContainer;
    private Map<String, String> currentParams;
    private BorderPane contentArea;
    
    // Layout principal avec sidebar (unique)
    private BorderPane mainLayout;
    private Map<String, Parent> contentViews;
    
    public NavigationManager(Stage stage) {
        this.primaryStage = stage;
        this.views = new HashMap<>();
        this.contentViews = new HashMap<>();
        this.history = new Stack<>();
        this.rootContainer = new StackPane();
        this.currentParams = new HashMap<>();
        this.currentScene = new Scene(rootContainer, 1400, 1000);
        
        // Créer le layout principal UNE SEULE FOIS
        mainLayout = new BorderPane();
        mainLayout.setLeft(new Sidebar(this));
        mainLayout.setBottom(new Footer());
        
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            if (cssPath != null) {
                this.currentScene.getStylesheets().add(cssPath);
                System.out.println("CSS chargé: " + cssPath);
            } else {
                System.out.println("CSS non trouvé");
            }
        } catch (Exception e) {
            System.out.println("Erreur chargement CSS: " + e.getMessage());
        }
        
        this.primaryStage.setScene(currentScene);
        this.primaryStage.setMinHeight(700);
        this.primaryStage.setMinWidth(1200);
    }
    
    public void registerView(String name, Parent view) {
        views.put(name, view);
        System.out.println("✅ Vue enregistrée: " + name);
    }
    
    public Parent getView(String name) {
        return views.get(name);
    }
    
    public Parent getViewInstance(String name) {
        return views.get(name);
    }
    
    public void setContentArea(BorderPane area) {
        this.contentArea = area;
    }
    
    public void registerProtectedContent(String name, Parent content) {
        contentViews.put(name, content);
        System.out.println("✅ Contenu protégé enregistré: " + name);
    }
    
    public void navigateTo(String name) {
        System.out.println("📍 Navigation vers: " + name);
        
        String baseName = name;
        if (name.contains("?")) {
            baseName = name.split("\\?")[0];
        }
        
        // Vérifier si c'est une vue protégée
        if (contentViews.containsKey(baseName)) {
            Parent content = contentViews.get(baseName);
            if (contentArea != null) {
                contentArea.setCenter(content);
                System.out.println("📦 Contenu affiché dans contentArea: " + baseName);
            } else {
                mainLayout.setCenter(content);
                System.out.println("📦 Contenu affiché dans mainLayout: " + baseName);
            }
            
            // S'assurer que mainLayout est affiché
            if (rootContainer.getChildren().isEmpty() || rootContainer.getChildren().get(0) != mainLayout) {
                final BorderPane finalLayout = mainLayout;
                final String finalBaseName = baseName;
                FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    rootContainer.getChildren().setAll(finalLayout);
                    history.push(finalBaseName);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootContainer);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                    primaryStage.sizeToScene();
                    System.out.println("✅ Navigation terminée vers: " + finalBaseName);
                });
                fadeOut.play();
            }
            return;
        }
        
        Parent view = views.get(baseName);
        if (view != null) {
            final Parent finalView = view;
            final String finalBaseName = baseName;
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootContainer);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                rootContainer.getChildren().setAll(finalView);
                history.push(finalBaseName);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootContainer);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                primaryStage.sizeToScene();
                System.out.println("✅ Navigation terminée vers: " + finalBaseName);
            });
            fadeOut.play();
        } else {
            System.err.println("❌ View not found: " + baseName);
            System.out.println("📋 Vues disponibles: " + views.keySet());
        }
    }
    
    public void navigateToWithParams(String baseName, Map<String, String> params) {
        String key = baseName + "_" + params.get("id");
        System.out.println("🔍 Navigation avec paramètres vers: " + key);
        
        boolean forceRefresh = params.containsKey("refresh") && "true".equals(params.get("refresh"));
        
        if (!views.containsKey(key) || forceRefresh) {
            if (forceRefresh && views.containsKey(key)) {
                views.remove(key);
                contentViews.remove(key);
                System.out.println("🔄 Rafraîchissement forcé de la vue: " + key);
            }
            
            Parent newView = null;
            
            switch (baseName) {
                case "commandeDetail":
                    int detailId = Integer.parseInt(params.get("id"));
                    System.out.println("📄 Création de la vue Detail pour la commande #" + detailId);
                    newView = new com.inapp.view.front.commande.Detail(this, detailId);
                    break;
                    
                case "commandeEdit":
                    int editId = Integer.parseInt(params.get("id"));
                    System.out.println("✏️ Création de la vue Edit pour la commande #" + editId);
                    newView = new com.inapp.view.front.commande.Edit(this, editId);
                    break;
                    
                case "commandeDelete":
                    int deleteId = Integer.parseInt(params.get("id"));
                    newView = new com.inapp.view.front.commande.Delete(this, deleteId);
                    break;
                    
                default:
                    System.err.println("❌ View inconnue: " + baseName);
                    return;
            }
            
            if (newView != null) {
                registerProtectedContent(key, newView);
                views.put(key, mainLayout);
                System.out.println("✅ Vue enregistrée: " + key);
            }
        }
        
        navigateTo(key);
    }
    
    public void goBack() {
        if (history.size() > 1) {
            history.pop();
            String previousView = history.peek();
            navigateTo(previousView);
        }
    }
    
    public String getCurrentView() {
        return history.isEmpty() ? null : history.peek();
    }
    
    public Stage getStage() {
        return primaryStage;
    }
    
    public void clearParams() {
        this.currentParams.clear();
    }
    
    public Map<String, String> getCurrentParams() {
        return currentParams;
    }
}