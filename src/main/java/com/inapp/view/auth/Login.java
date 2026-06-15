package com.inapp.view.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.SessionManager;
import com.inapp.MainApplication;

public class Login extends VBox {
    
    private NavigationManager navigationManager;
    private MainApplication mainApp;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    
    public Login(NavigationManager navManager, MainApplication app) {
        this.navigationManager = navManager;
        this.mainApp = app;
        setupUI();
    }
    
    private void setupUI() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");
        
        VBox card = new VBox(20);
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        card.setPadding(new Insets(40));
        
        Label title = new Label("PowerStock");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #E66239;");
        title.setAlignment(Pos.CENTER);
        
        Label subtitle = new Label("Système de gestion de facturation");
        subtitle.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        subtitle.setAlignment(Pos.CENTER);
        
        usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        Button loginBtn = new Button("Se connecter");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 12px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand;");
        loginBtn.setOnAction(e -> handleLogin());
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        card.getChildren().addAll(title, subtitle, usernameField, passwordField, loginBtn, errorLabel);
        
        StackPane centerPane = new StackPane(card);
        centerPane.setAlignment(Pos.CENTER);
        getChildren().add(centerPane);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            errorLabel.setVisible(true);
            return;
        }
        
        if ("admin".equals(username) && "admin".equals(password)) {
            SessionManager.getInstance().setUser(1, username, "super_admin", "admin@inapp.com", "Admin Super");
            mainApp.showMainScene();
        } else if ("user".equals(username) && "user".equals(password)) {
            SessionManager.getInstance().setUser(2, username, "admin", "user@inapp.com", "Utilisateur Normal");
            mainApp.showMainScene();
        } else {
            errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
            errorLabel.setVisible(true);
        }
    }
}