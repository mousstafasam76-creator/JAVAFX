package com.inapp.view.auth;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.SessionManager;

public class Logout extends StackPane {
    
    public Logout(NavigationManager navigationManager) {
        setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label("🚪");
        iconLabel.setStyle("-fx-font-size: 64px;");
        
        Label messageLabel = new Label("Déconnexion en cours...");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        content.getChildren().addAll(iconLabel, messageLabel);
        getChildren().add(content);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> {
            SessionManager.getInstance().clearSession();
            navigationManager.navigateTo("login");
        });
        delay.play();
    }
}