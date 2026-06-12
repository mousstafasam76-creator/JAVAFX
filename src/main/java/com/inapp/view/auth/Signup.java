package com.inapp.view.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;

public class Signup extends VBox {
    
    private NavigationManager navigationManager;
    private TextField fullNameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private CheckBox termsCheckBox;
    private Label errorLabel;
    
    public Signup(NavigationManager navManager) {
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
        
        Label title = new Label("Create your account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");
        title.setAlignment(Pos.CENTER);
        
        fullNameField = new TextField();
        fullNameField.setPromptText("Full name");
        fullNameField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        emailField = new TextField();
        emailField.setPromptText("Email address");
        emailField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Repeat password");
        confirmPasswordField.setStyle("-fx-padding: 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ddd;");
        
        termsCheckBox = new CheckBox("I agree to the terms and privacy");
        termsCheckBox.setStyle("-fx-font-size: 12px;");
        
        Button signupBtn = new Button("Sign up");
        signupBtn.setMaxWidth(Double.MAX_VALUE);
        signupBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-padding: 12px; -fx-background-radius: 8px; -fx-font-weight: bold;");
        signupBtn.setOnAction(e -> {
            if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty() || 
                passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
            } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                errorLabel.setText("Passwords do not match");
                errorLabel.setVisible(true);
            } else if (!termsCheckBox.isSelected()) {
                errorLabel.setText("You must agree to the terms");
                errorLabel.setVisible(true);
            } else {
                AlertUtils.showSuccessMessage("Account created successfully!");
                navigationManager.navigateTo("signin");
            }
        });
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        HBox signinLink = new HBox(5);
        signinLink.setAlignment(Pos.CENTER);
        Label haveAccountLabel = new Label("Already have an account?");
        Hyperlink signinHyper = new Hyperlink("Sign in");
        signinHyper.setOnAction(e -> navigationManager.navigateTo("signin"));
        signinLink.getChildren().addAll(haveAccountLabel, signinHyper);
        
        card.getChildren().addAll(title, fullNameField, emailField, passwordField, confirmPasswordField, 
                                   termsCheckBox, signupBtn, errorLabel, signinLink);
        
        StackPane centerPane = new StackPane(card);
        centerPane.setAlignment(Pos.CENTER);
        getChildren().add(centerPane);
    }
}