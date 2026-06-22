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
        // ✅ FENETRE REDUITE : 1200x800 au lieu de 1400x1000
        this.currentScene = new Scene(rootContainer, 1200, 800);
        
        mainLayout = new BorderPane();
        mainLayout.setLeft(new Sidebar(this));
        mainLayout.setBottom(new Footer());
        
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            if (cssPath != null) {
                this.currentScene.getStylesheets().add(cssPath);
                System.out.println("CSS charge: " + cssPath);
            }
        } catch (Exception e) {
            System.out.println("Erreur chargement CSS: " + e.getMessage());
        }
        
        this.primaryStage.setScene(currentScene);
        // ✅ TAILLE MINIMALE REDUITE
        this.primaryStage.setMinHeight(200);
        this.primaryStage.setMinWidth(500);
    }
    
    public void registerView(String name, Parent view) {
        views.put(name, view);
        System.out.println("Vue enregistree: " + name);
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
    
    public BorderPane getContentArea() {
        return this.contentArea;
    }
    
    public void registerProtectedContent(String name, Parent content) {
        contentViews.put(name, content);
        System.out.println("Contenu protege enregistre: " + name);
    }
    
    public void navigateTo(String name) {
        System.out.println("Navigation vers: " + name);
        
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
            if (baseName.equals("clientView") || baseName.equals("clientEdit") ||
                baseName.equals("categoryDetail") || baseName.equals("categoryEdit") || baseName.equals("categoryDelete")) {
                navigateToWithParams(baseName, params);
                return;
            }
        }
        
        String fullName = name;
        
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
        
        if (contentViews.containsKey(fullName)) {
            Parent content = contentViews.get(fullName);
            if (contentArea != null) {
                contentArea.setCenter(content);
                System.out.println("Contenu avec parametres affiche dans contentArea: " + fullName);
                history.push(fullName);
            }
            return;
        }
        
        if (contentViews.containsKey(baseName)) {
            Parent content = contentViews.get(baseName);
            if (contentArea != null) {
                contentArea.setCenter(content);
                System.out.println("Contenu affiche dans contentArea: " + baseName);
                history.push(baseName);
            } else {
                mainLayout.setCenter(content);
                System.out.println("Contenu affiche dans mainLayout: " + baseName);
            }
            
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
                    System.out.println("Navigation terminee vers: " + finalBaseName);
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
                System.out.println("Navigation terminee vers: " + finalBaseName);
            });
            fadeOut.play();
        } else {
            System.err.println("View not found: " + name);
            System.out.println("Vues disponibles: " + contentViews.keySet());
        }
    }
    
    public void navigateToWithParams(String baseName, Map<String, String> params) {
        System.out.println("Navigation avec parametres vers: " + baseName);
        
        if (params == null || !params.containsKey("id")) {
            navigateTo(baseName);
            return;
        }
        
        String viewName = baseName + "_" + params.get("id");
        System.out.println("Vue avec parametres: " + viewName);
        
        if (!contentViews.containsKey(viewName)) {
            Parent newView = null;
            try {
                int id = Integer.parseInt(params.get("id"));
                switch (baseName) {
                    case "commandeDetail":
                    case "commandeDetails":
                        System.out.println("Creation de la vue Detail pour la commande #" + id);
                        newView = new com.inapp.view.front.commande.Detail(this, id);
                        break;
                        
                    case "commandeEdit":
                        System.out.println("Creation de la vue Edit pour la commande #" + id);
                        newView = new com.inapp.view.front.commande.Edit(this, id);
                        break;
                        
                    case "commandeDelete":
                        System.out.println("Creation de la vue Delete pour la commande #" + id);
                        newView = new com.inapp.view.front.commande.Delete(this, id);
                        break;
                    
                    case "clientView":
                        System.out.println("Creation de la vue ViewClient pour le client #" + id);
                        newView = new com.inapp.view.front.Client.ViewClientView(this, id);
                        break;
                        
                    case "clientEdit":
                        System.out.println("Creation de la vue EditClient pour le client #" + id);
                        newView = new com.inapp.view.front.Client.EditClientView(this, id);
                        break;
                    
                    case "categoryDetail":
                        System.out.println("Creation de la vue Detail pour la categorie #" + id);
                        newView = new com.inapp.view.front.Category.CategoryDetailView(this, id);
                        break;
                        
                    case "categoryEdit":
                        System.out.println("Creation de la vue Edit pour la categorie #" + id);
                        newView = new com.inapp.view.front.Category.CategoryEditView(this, id);
                        break;
                        
                    case "categoryDelete":
                        System.out.println("Creation de la vue Delete pour la categorie #" + id);
                        newView = new com.inapp.view.front.Category.CategoryDeleteView(this, id);
                        break;
                        
                    default:
                        System.err.println("View inconnue: " + baseName);
                        return;
                }
            } catch (Exception e) {
                System.err.println("Erreur creation vue: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            if (newView != null) {
                contentViews.put(viewName, newView);
                System.out.println("Vue enregistree: " + viewName);
            }
        }
        
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
    
    public void clearAllViews() {
        contentViews.clear();
        history.clear();
        currentParams.clear();
        System.out.println("Cache vide");
    }
    
    public void clearParams() {
        this.currentParams.clear();
    }
}