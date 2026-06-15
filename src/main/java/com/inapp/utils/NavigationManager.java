package com.inapp.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private BorderPane contentArea; // Pour le layout fixe
    
    public NavigationManager(Stage stage) {
        this.primaryStage = stage;
        this.views = new HashMap<>();
        this.history = new Stack<>();
        this.rootContainer = new StackPane();
        this.currentParams = new HashMap<>();
        this.currentScene = new Scene(rootContainer, 1400, 1000);
        
        // Chargement du CSS
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            this.currentScene.getStylesheets().add(cssPath);
            System.out.println("CSS chargé: " + cssPath);
        } catch (Exception e) {
            System.out.println("Erreur chargement CSS: " + e.getMessage());
        }
        
        this.primaryStage.setScene(currentScene);
        this.primaryStage.setMinHeight(700);
        this.primaryStage.setMinWidth(1200);
    }
    
    public void registerView(String name, Parent view) {
        views.put(name, view);
    }
    
    public Parent getView(String name) {
        return views.get(name);
    }
    
    public void setContentArea(BorderPane area) {
        this.contentArea = area;
    }
    
    public void navigateTo(String name) {
        Parent view = views.get(name);
        if (view != null) {
            // Si on a un contentArea (layout fixe), on met à jour seulement le centre
            if (contentArea != null && !isFullPageView(name)) {
                contentArea.setCenter(view);
                history.push(name);
            } else {
                // Sinon, on remplace tout le contenu
                FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    rootContainer.getChildren().setAll(view);
                    history.push(name);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootContainer);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                    primaryStage.sizeToScene();
                });
                fadeOut.play();
            }
        } else {
            System.err.println("View not found: " + name);
        }
    }
    
    private boolean isFullPageView(String name) {
        // Les vues d'authentification sont en plein écran
        return name.equals("login") || name.equals("signin") || 
               name.equals("signup") || name.equals("logout");
    }
    
    public void navigateToWithParams(String viewName, Map<String, String> params) {
        if (params != null) {
            this.currentParams = params;
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
    
    public void clearParams() {
        this.currentParams.clear();
    }
}