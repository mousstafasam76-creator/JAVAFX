package com.inapp.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.SessionManager;

public class AdminSidebar extends StackPane {

    private NavigationManager navManager;
    private int pendingCategories = 3; // fictif

    public AdminSidebar(NavigationManager navManager) {
        this.navManager = navManager;

        // ===== TAILLE FIXE (comme Sidebar.java) =====
        setPrefSize(280,900);
        setMinSize(280, 900);
        setMaxWidth(280);
        setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");
        setAlignment(Pos.TOP_CENTER);

        // ===== CONTENEUR INTERNE =====
        VBox innerBox = new VBox(0);
        innerBox.setFillWidth(true);

        // --- En-tête ---
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(25, 20, 25, 20));
        header.setStyle("-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 0 0 1 0;");
        Label appName = new Label("InApp");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label appSub = new Label("Panel Super Admin");
        appSub.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 11px;");
        header.getChildren().addAll(appName, appSub);

        // --- Info utilisateur ---
        VBox userBox = new VBox(10);
        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(20));
        userBox.setStyle("-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 0 0 1 0;");

        StackPane avatar = new StackPane();
        avatar.setPrefSize(80, 80);
        avatar.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 40;");
        Label avatarIcon = new Label("👑");
        avatarIcon.setStyle("-fx-font-size: 40px;");
        avatar.getChildren().add(avatarIcon);

        String userName = SessionManager.getInstance().getCurrentUserFullName();
        if (userName.isEmpty()) userName = "Super Admin";
        Label userNameLabel = new Label(userName);
        userNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 600;");
        Label badge = new Label("👑 Super Admin");
        badge.setStyle("-fx-background-color: #ff4757; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 20; -fx-font-size: 10px;");
        userBox.getChildren().addAll(avatar, userNameLabel, badge);

        // --- Menu (avec ScrollPane) ---
        VBox menuBox = new VBox(5);
        menuBox.setPadding(new Insets(15, 0, 15, 0));
        menuBox.setFillWidth(true);

        menuBox.getChildren().add(createSectionTitle("Tableau de bord"));
        menuBox.getChildren().add(createNavItem("📊", "Dashboard", "adminDashboard"));

        menuBox.getChildren().add(createSectionTitle("👥 Utilisateurs"));
        menuBox.getChildren().add(createNavItem("👤", "Liste des utilisateurs", "usersList"));
        menuBox.getChildren().add(createNavItem("➕", "Ajouter un utilisateur", "userAdd"));

        menuBox.getChildren().add(createSectionTitle("🏷️ Catégories"));
        menuBox.getChildren().add(createNavItem("📋", "Liste des catégories", "categoriesList"));
        menuBox.getChildren().add(createNavItem("➕", "Ajouter une catégorie", "categoryAdd"));
        HBox pendingBox = createNavItemWithBadge("⏳", "Validation en attente", "categoriesPending", pendingCategories);
        menuBox.getChildren().add(pendingBox);

        menuBox.getChildren().add(createSectionTitle("📦 Produits"));
        menuBox.getChildren().add(createNavItem("📦", "Liste des produits", "productsList"));
        menuBox.getChildren().add(createNavItem("➕", "Ajouter un produit", "productAdd"));

        menuBox.getChildren().add(createSectionTitle("📊 Analyses"));
        menuBox.getChildren().add(createNavItem("📈", "Statistiques", "statistics"));

        // Séparateur et Déconnexion
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
        VBox.setMargin(sep, new Insets(10, 0, 5, 0));
        menuBox.getChildren().add(sep);
        menuBox.getChildren().add(createNavItem("🚪", "Déconnexion", "login"));

        // ScrollPane pour le menu (comme dans Sidebar.java)
        ScrollPane scrollPane = new ScrollPane(menuBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        // Force le ScrollPane à prendre toute la hauteur disponible
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Assemblage
        innerBox.getChildren().addAll(header, userBox, scrollPane);
        getChildren().add(innerBox);
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px; -fx-font-weight: bold;");
        label.setPadding(new Insets(15, 20, 5, 20));
        return label;
    }

    private Button createNavItem(String icon, String text, String viewName) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 20, 12, 20));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 13px;");
        content.getChildren().addAll(iconLabel, textLabel);
        btn.setGraphic(content);

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-padding: 12 20 12 25; -fx-cursor: hand;");
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 12 20 12 20; -fx-cursor: hand;");
        });
        btn.setOnAction(e -> navManager.navigateTo(viewName));
        return btn;
    }

    private HBox createNavItemWithBadge(String icon, String text, String viewName, int badgeCount) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12, 20, 12, 20));
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(String.valueOf(badgeCount));
        badge.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 20; -fx-font-size: 11px;");
        box.getChildren().addAll(iconLabel, textLabel, spacer, badge);
        box.setStyle("-fx-cursor: hand;");
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-cursor: hand;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));
        box.setOnMouseClicked(e -> navManager.navigateTo(viewName));
        return box;
    }
}