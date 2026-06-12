package com.inapp.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NavigationManager {

    private final Stage primaryStage;
    private final Map<String, Parent> views;
    private final Scene currentScene;
    private final Stack<String> history;
    private final StackPane rootContainer;
    private FadeTransition currentTransition; // pour éviter les transitions simultanées

    public NavigationManager(Stage stage) {
        this.primaryStage = stage;
        this.views = new HashMap<>();
        this.history = new Stack<>();
        this.rootContainer = new StackPane();
        // Taille de la scène (1400x1000)
        this.currentScene = new Scene(rootContainer, 1400, 1000);

        // Chargement du CSS
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            currentScene.getStylesheets().add(cssPath);
            System.out.println("CSS chargé : " + cssPath);
        } catch (Exception e) {
            System.out.println("Erreur chargement CSS : " + e.getMessage());
        }

        primaryStage.setScene(currentScene);
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(1200);
    }

    /**
     * Enregistre une vue avec son identifiant.
     */
    public void registerView(String name, Parent view) {
        views.put(name, view);
    }

    /**
     * Définit la vue initiale (sans ajout dans l'historique).
     */
    public void setInitialView(String name) {
        Parent view = views.get(name);
        if (view != null) {
            rootContainer.getChildren().setAll(view);
            history.clear();
            history.push(name);
        } else {
            System.err.println("Vue introuvable : " + name);
        }
    }

    /**
     * Navigue vers une vue identifiée par son nom.
     * Évite d'empiler deux fois la même vue et gère l'historique correctement.
     */
    public void navigateTo(String name) {
        // Ne rien faire si on est déjà sur cette vue
        if (!history.isEmpty() && history.peek().equals(name)) {
            return;
        }

        Parent view = views.get(name);
        if (view == null) {
            System.err.println("Vue introuvable : " + name);
            return;
        }

        // Annuler toute transition en cours pour éviter les conflits
        if (currentTransition != null && currentTransition.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            currentTransition.stop();
        }

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
            currentTransition = fadeIn;
        });
        fadeOut.play();
        currentTransition = fadeOut;
    }

    /**
     * Retourne à la vue précédente (sans repousser l'ancienne).
     */
    public void goBack() {
        if (history.size() > 1) {
            // On récupère la vue précédente sans la retirer tout de suite
            String current = history.pop();
            String previous = history.peek();

            Parent previousView = views.get(previous);
            if (previousView == null) {
                // Restauration en cas d'erreur
                history.push(current);
                System.err.println("Impossible de revenir en arrière : vue '" + previous + "' introuvable");
                return;
            }

            // Annuler la transition en cours
            if (currentTransition != null && currentTransition.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                currentTransition.stop();
            }

            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootContainer);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                rootContainer.getChildren().setAll(previousView);
                // Ne PAS repush "previous" – elle est déjà au sommet

                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootContainer);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                currentTransition = fadeIn;
            });
            fadeOut.play();
            currentTransition = fadeOut;
        }
    }

    /**
     * Retourne le nom de la vue actuellement affichée.
     */
    public String getCurrentView() {
        return history.isEmpty() ? null : history.peek();
    }

    public Stage getStage() {
        return primaryStage;
    }
    public Parent getView(String name) {
    return views.get(name);
}
}