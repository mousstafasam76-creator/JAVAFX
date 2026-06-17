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
    private Map<String, Parent> contentViews;
    private Scene currentScene;
    private Stack<String> history;
    private StackPane rootContainer;
    private Map<String, String> currentParams;
    private BorderPane contentArea;
    private BorderPane mainLayout;
    
    public NavigationManager(Stage stage) {
        this.primaryStage = stage;
        this.views = new HashMap<>();
        this.contentViews = new HashMap<>();
        this.history = new Stack<>();
        this.rootContainer = new StackPane();
        this.currentParams = new HashMap<>();
        this.currentScene = new Scene(rootContainer, 1400, 1000);
        
        mainLayout = new BorderPane();
        mainLayout.setLeft(new Sidebar(this));
        mainLayout.setBottom(new Footer());
        
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            if (cssPath != null) {
                this.currentScene.getStylesheets().add(cssPath);
                System.out.println("CSS chargé: " + cssPath);
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
        
        // Gérer les URLs avec paramètres (ex: clientView?id=1)
        String baseName = name;
        Map<String, String> params = new HashMap<>();
        
        if (name.contains("?")) {
            String[] parts = name.split("\\?");
            baseName = parts[0];
            String[] paramPairs = parts[1].split("&");
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
            // Si c'est une vue client avec paramètres, utiliser navigateToWithParams
            if (baseName.equals("clientView") || baseName.equals("clientEdit")) {
                navigateToWithParams(baseName, params);
                return;
            }
        }
        
        String fullName = name;
        
        // Extraire le nom de base si c'est une vue avec paramètres (ex: commandeDetails_1)
        if (name.contains("_")) {
            String[] parts = name.split("_");
            if (parts.length == 2) {
                try {
                    Integer.parseInt(parts[1]);
                    baseName = parts[0];
                    fullName = name;
                } catch (NumberFormatException e) {
                    baseName = name;
                }
            }
        }
        
        // Vérifier d'abord si c'est une vue avec paramètres (commandeDetails_1)
        if (contentViews.containsKey(fullName)) {
            Parent content = contentViews.get(fullName);
            if (contentArea != null) {
                contentArea.setCenter(content);
                System.out.println("📦 Contenu avec paramètres affiché dans contentArea: " + fullName);
                history.push(fullName);
            }
            return;
        }
        
        // Vérifier si c'est une vue protégée (sans paramètres)
        if (contentViews.containsKey(baseName)) {
            Parent content = contentViews.get(baseName);
            if (contentArea != null) {
                contentArea.setCenter(content);
                System.out.println("📦 Contenu affiché dans contentArea: " + baseName);
                history.push(baseName);
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
            System.err.println("❌ View not found: " + name);
            System.out.println("📋 Vues disponibles: " + contentViews.keySet());
        }
    }
    
    public void navigateToWithParams(String baseName, Map<String, String> params) {
        System.out.println("🔍 Navigation avec paramètres vers: " + baseName);
        
        if (params == null || !params.containsKey("id")) {
            navigateTo(baseName);
            return;
        }
        
        String viewName = baseName + "_" + params.get("id");
        System.out.println("🔍 Vue avec paramètres: " + viewName);
        
        // Vérifier si la vue existe déjà
        if (!contentViews.containsKey(viewName)) {
            Parent newView = null;
            try {
                int id = Integer.parseInt(params.get("id"));
                switch (baseName) {
                    case "commandeDetail":
                    case "commandeDetails":
                        System.out.println("📄 Création de la vue Detail pour la commande #" + id);
                        newView = new com.inapp.view.front.commande.Detail(this, id);
                        break;
                        
                    case "commandeEdit":
                        System.out.println("✏️ Création de la vue Edit pour la commande #" + id);
                        newView = new com.inapp.view.front.commande.Edit(this, id);
                        break;
                        
                    case "commandeDelete":
                        System.out.println("🗑️ Création de la vue Delete pour la commande #" + id);
                        newView = new com.inapp.view.front.commande.Delete(this, id);
                        break;
                        
                    case "clientView":
                        System.out.println("👤 Création de la vue ViewClient pour le client #" + id);
                        newView = new com.inapp.view.front.Client.ViewClientView(this, id);
                        break;
                        
                    case "clientEdit":
                        System.out.println("✏️ Création de la vue EditClient pour le client #" + id);
                        newView = new com.inapp.view.front.Client.EditClientView(this, id);
                        break;
                        
                    default:
                        System.err.println("❌ View inconnue: " + baseName);
                        return;
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur création vue: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            if (newView != null) {
                contentViews.put(viewName, newView);
                System.out.println("✅ Vue enregistrée: " + viewName);
            }
        }
        
        // Naviguer vers la vue avec l'ID
        navigateTo(viewName);
    }
    
    public Map<String, String> getCurrentParams() {
        return currentParams;
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
}