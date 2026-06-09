package com.inapp.view.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;

public class Signin extends VBox {
    
    private NavigationManager navigationManager;
    private TextField emailField;
    private PasswordField passwordField;
    private CheckBox rememberCheckBox;
    private Label errorLabel;
    
    public Signin(NavigationManager navManager) {
        this.navigationManager = navManager;
        setupUI();
    }
    
    private void setupUI() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #f5f7fa, #e9ecef);");
        
        VBox card = new VBox(20);
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px;");
        card.setPadding(new Insets(40));
        
        Label title = new Label("Sign in to your account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");
        title.setAlignment(Pos.CENTER);
        
        emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        rememberCheckBox = new CheckBox("Remember me");
        rememberCheckBox.setStyle("-fx-font-size: 12px;");
        
        Button signinBtn = new Button("Sign in");
        signinBtn.setMaxWidth(Double.MAX_VALUE);
        signinBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-padding: 12px; -fx-background-radius: 8px; -fx-font-weight: bold;");
        signinBtn.setOnAction(e -> {
            if ("admin@inapp.com".equals(emailField.getText()) && "admin".equals(passwordField.getText())) {
                navigationManager.navigateTo("adminDashboard");
            } else {
                errorLabel.setText("Invalid email or password");
                errorLabel.setVisible(true);
            }
        });
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        HBox signupLink = new HBox(5);
        signupLink.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("Don't have an account?");
        Hyperlink signupHyper = new Hyperlink("Sign up");
        signupHyper.setOnAction(e -> navigationManager.navigateTo("signup"));
        signupLink.getChildren().addAll(noAccountLabel, signupHyper);
        
        card.getChildren().addAll(title, emailField, passwordField, rememberCheckBox, signinBtn, errorLabel, signupLink);
        
        StackPane centerPane = new StackPane(card);
        centerPane.setAlignment(Pos.CENTER);
        getChildren().add(centerPane);
    }
}