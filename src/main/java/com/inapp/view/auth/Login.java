package com.inapp.view.auth;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.SessionManager;
import com.inapp.MainApplication;

public class Login extends VBox {
    
    private NavigationManager navigationManager;
    private MainApplication mainApp;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    
    public Login(NavigationManager navManager) {
        this.navigationManager = navManager;
        setupUI();
    }

    public Login(NavigationManager navManager, MainApplication app) {
        this.navigationManager = navManager;
        this.mainApp = app;
        setupUI();
    }
    
    private void setupUI() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0));
        
        // Fond orange vif avec dégradé
        setStyle("-fx-background-color: linear-gradient(to bottom right, #FF6B00, #FF8C00, #E66239);");
        
        // === CARTE PRINCIPALE ===
        VBox card = new VBox(25);
        card.setMaxWidth(420);
        card.setMinWidth(380);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20px; " +
            "-fx-padding: 40px 35px;"
        );
        card.setAlignment(Pos.CENTER);
        
        // Ombre portée
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(20);
        shadow.setOffsetY(10);
        card.setEffect(shadow);
        
        // === LOGO / ICÔNE ===
        StackPane logoContainer = new StackPane();
        Circle logoCircle = new Circle(35);
        logoCircle.setFill(Color.web("#E66239"));
        
        Label logoIcon = new Label("⚡");
        logoIcon.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
        logoContainer.getChildren().addAll(logoCircle, logoIcon);
        
        // Animation du logo
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.8), logoContainer);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        st.setDelay(Duration.millis(200));
        st.play();
        
        // === TITRE ===
        Label title = new Label("PowerStock");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #E66239;");
        title.setAlignment(Pos.CENTER);
        
        // === SOUS-TITRE ===
        Label subtitle = new Label("Système de Gestion & Facturation");
        subtitle.setFont(Font.font("System", 13));
        subtitle.setStyle("-fx-text-fill: #888;");
        subtitle.setAlignment(Pos.CENTER);
        
        // === SÉPARATEUR ===
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #eee;");
        sep.setMaxWidth(250);
        
        // === CHAMP USERNAME ===
        HBox usernameBox = new HBox(12);
        usernameBox.setAlignment(Pos.CENTER_LEFT);
        usernameBox.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 12px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 12px; " +
            "-fx-border-width: 1.5px;"
        );
        
        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 16px;");
        
        usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 5px 0;"
        );
        usernameField.setPrefWidth(250);
        HBox.setHgrow(usernameField, Priority.ALWAYS);
        usernameBox.getChildren().addAll(userIcon, usernameField);
        
        // Effet focus
        usernameField.focusedProperty().addListener((obs, old, val) -> {
            usernameBox.setStyle(
                "-fx-background-color: " + (val ? "#fff5f0" : "#f8f9fa") + "; " +
                "-fx-background-radius: 12px; " +
                "-fx-padding: 8px 16px; " +
                "-fx-border-color: " + (val ? "#E66239" : "#e0e0e0") + "; " +
                "-fx-border-radius: 12px; " +
                "-fx-border-width: 1.5px;"
            );
        });
        
        // === CHAMP PASSWORD ===
        HBox passwordBox = new HBox(12);
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        passwordBox.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 12px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 12px; " +
            "-fx-border-width: 1.5px;"
        );
        
        Label passIcon = new Label("🔒");
        passIcon.setStyle("-fx-font-size: 16px;");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 5px 0;"
        );
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        passwordBox.getChildren().addAll(passIcon, passwordField);
        
        // Effet focus
        passwordField.focusedProperty().addListener((obs, old, val) -> {
            passwordBox.setStyle(
                "-fx-background-color: " + (val ? "#fff5f0" : "#f8f9fa") + "; " +
                "-fx-background-radius: 12px; " +
                "-fx-padding: 8px 16px; " +
                "-fx-border-color: " + (val ? "#E66239" : "#e0e0e0") + "; " +
                "-fx-border-radius: 12px; " +
                "-fx-border-width: 1.5px;"
            );
        });
        
        // === MESSAGE D'ERREUR ===
        errorLabel = new Label();
        errorLabel.setStyle(
            "-fx-text-fill: #dc3545; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #ffe0e0; " +
            "-fx-padding: 8px 15px; " +
            "-fx-background-radius: 8px;"
        );
        errorLabel.setVisible(false);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setAlignment(Pos.CENTER);
        
        // === BOUTON CONNEXION ===
        Button loginBtn = new Button("SE CONNECTER");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
        loginBtn.setStyle(
            "-fx-background-color: #E66239; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 14px; " +
            "-fx-background-radius: 12px; " +
            "-fx-cursor: hand; " +
            "-fx-letter-spacing: 1px;"
        );
        loginBtn.setOnAction(e -> handleLogin());
        
        // Effet hover
        loginBtn.setOnMouseEntered(e -> 
            loginBtn.setStyle(
                "-fx-background-color: #c5542e; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 14px; " +
                "-fx-background-radius: 12px; " +
                "-fx-cursor: hand; " +
                "-fx-letter-spacing: 1px;"
            )
        );
        loginBtn.setOnMouseExited(e -> 
            loginBtn.setStyle(
                "-fx-background-color: #E66239; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 14px; " +
                "-fx-background-radius: 12px; " +
                "-fx-cursor: hand; " +
                "-fx-letter-spacing: 1px;"
            )
        );
        
        // Animation du bouton
        loginBtn.setOnMousePressed(e -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(100), loginBtn);
            press.setToX(0.97);
            press.setToY(0.97);
            press.play();
        });
        loginBtn.setOnMouseReleased(e -> {
            ScaleTransition release = new ScaleTransition(Duration.millis(100), loginBtn);
            release.setToX(1);
            release.setToY(1);
            release.play();
        });
        
        // === INFO COMPLÉMENTAIRE ===
        Label infoLabel = new Label("🔑 admin/admin | user/user");
        infoLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");
        infoLabel.setAlignment(Pos.CENTER);
        
        // === ASSEMBLAGE ===
        card.getChildren().addAll(
            logoContainer,
            title,
            subtitle,
            sep,
            usernameBox,
            passwordBox,
            errorLabel,
            loginBtn,
            infoLabel
        );
        
        // Animation d'entrée de la carte
        card.setOpacity(0);
        card.setTranslateY(30);
        
        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), card);
        tt.setFromY(30);
        tt.setToY(0);
        
        ft.play();
        tt.play();
        
        StackPane centerPane = new StackPane(card);
        centerPane.setAlignment(Pos.CENTER);
        getChildren().add(centerPane);
        
        // Focus automatique sur le champ username
        PauseTransition focusDelay = new PauseTransition(Duration.millis(800));
        focusDelay.setOnFinished(e -> usernameField.requestFocus());
        focusDelay.play();
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs");
            return;
        }
        
        // Essayer la connexion via la base de donnees
        boolean loggedIn = verifierConnexionBD(username, password);
        
        // Fallback : connexion en dur si la BD echoue
        if (!loggedIn) {
            if ("admin".equals(username) && "admin".equals(password)) {
                SessionManager.getInstance().setUser(1, username, "admin", "admin@powerstock.com", "Administrateur");
                loggedIn = true;
            } else if ("user".equals(username) && "user".equals(password)) {
                SessionManager.getInstance().setUser(2, username, "user", "user@powerstock.com", "Utilisateur");
                loggedIn = true;
            }
        }
        
        if (loggedIn) {
            String role = SessionManager.getInstance().getCurrentUserRole();
            System.out.println("Connexion reussie: " + username + " (role: " + role + ")");
            
            // Animation de succès
            errorLabel.setText("✅ Connexion réussie !");
            errorLabel.setStyle(
                "-fx-text-fill: #155724; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: #d4edda; " +
                "-fx-padding: 8px 15px; " +
                "-fx-background-radius: 8px;"
            );
            errorLabel.setVisible(true);
            
            PauseTransition delay = new PauseTransition(Duration.millis(500));
            delay.setOnFinished(e -> {
                if (mainApp != null) {
                    mainApp.showMainScene();
                } else {
                    if ("admin".equals(role) || "super_admin".equals(role)) {
                        navigationManager.navigateTo("adminDashboard");
                    } else {
                        navigationManager.navigateTo("dashboard");
                    }
                }
            });
            delay.play();
            
        } else {
            afficherErreur("Nom d'utilisateur ou mot de passe incorrect");
            // Animation de secousse
            shakeAnimation(passwordField);
        }
    }
    
    private void afficherErreur(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle(
            "-fx-text-fill: #dc3545; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #ffe0e0; " +
            "-fx-padding: 8px 15px; " +
            "-fx-background-radius: 8px;"
        );
        errorLabel.setVisible(true);
    }
    
    private void shakeAnimation(Control field) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), field);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> field.setTranslateX(0));
        shake.play();
    }
    
    private boolean verifierConnexionBD(String username, String password) {
        try {
            java.sql.Connection conn = com.inapp.config.DatabaseConfig.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT id, username, password, role, email, full_name FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role") != null ? rs.getString("role") : "user";
                String email = rs.getString("email") != null ? rs.getString("email") : "";
                String fullName = rs.getString("full_name") != null ? rs.getString("full_name") : username;
                
                System.out.println("Connexion BD reussie: " + username + " (role: " + role + ")");
                SessionManager.getInstance().setUser(id, username, role, email, fullName);
                conn.close();
                return true;
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur connexion BD: " + e.getMessage());
        }
        return false;
    }
}