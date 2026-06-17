package com.inapp.view.components;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import java.util.HashMap;
import java.util.Map;

public class Sidebar extends StackPane {

    private NavigationManager navigationManager;
    private String currentPage = "";
    private Map<String, Button> menuButtons;
    private boolean isCollapsed = false;
    private double expandedWidth = 260;
    private double collapsedWidth = 70;
    private HBox logoFullBox;
    private ImageView logoSmall;
    private VBox navContainer;
    private HBox logoArea;
    private VBox contentBox;

    public Sidebar(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.menuButtons = new HashMap<>();
        setPrefWidth(expandedWidth);
        setMinWidth(expandedWidth);
        setMaxWidth(expandedWidth);
        setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");
        setAlignment(Pos.TOP_CENTER);

        setupUI();
        setCurrentPage("dashboard");
    }

    private void setupUI() {
        contentBox = new VBox(0);
        contentBox.setFillWidth(true);

        logoArea = new HBox(0);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(12, 10, 12, 10));
        logoArea.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        logoArea.setPrefHeight(60);
        logoArea.setMinHeight(60);

        logoFullBox = new HBox(8);
        logoFullBox.setAlignment(Pos.CENTER_LEFT);
        boolean logoLoaded = false;
        try {
            java.io.InputStream logoStream = getClass().getResourceAsStream("/images/logo-icon2.png");
            if (logoStream != null) {
                Image logoImage = new Image(logoStream);
                if (!logoImage.isError()) {
                    ImageView logoView = new ImageView(logoImage);
                    logoView.setFitHeight(28);
                    logoView.setPreserveRatio(true);
                    logoFullBox.getChildren().add(logoView);
                    logoLoaded = true;
                }
            }
        } catch (Exception e) { /* fallback */ }
        if (!logoLoaded) {
            Label logoIcon = new Label("📊");
            logoIcon.setStyle("-fx-font-size: 22px;");
            Label logoText = new Label("PowerStock");
            logoText.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2a5298;");
            logoFullBox.getChildren().addAll(logoIcon, logoText);
        }
        logoFullBox.setVisible(true);

        Button toggleBtn = new Button("☰");
        toggleBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #6c757d; " +
            "-fx-font-size: 20px; -fx-cursor: hand; -fx-padding: 5;"
        );
        toggleBtn.setOnAction(e -> toggleCollapse());
        HBox.setHgrow(toggleBtn, Priority.NEVER);
        toggleBtn.setMaxWidth(40);
        toggleBtn.setMinWidth(40);

        Region logoSpacer = new Region();
        HBox.setHgrow(logoSpacer, Priority.ALWAYS);
        logoArea.getChildren().addAll(logoFullBox, logoSpacer, toggleBtn);

        navContainer = new VBox(3);
        navContainer.setPadding(new Insets(5, 0, 20, 0));

        // Principal
        navContainer.getChildren().add(createSectionLabel("Principal"));
        navContainer.getChildren().add(createNavButton("🏠", "Tableau de bord", "dashboard"));

        // Ventes
        navContainer.getChildren().add(createSectionLabel("Ventes"));
        navContainer.getChildren().add(createNavButton("🚚", "Commandes", "commandesList"));
        navContainer.getChildren().add(createNavButton("📄", "Factures", "factures"));

        // Catalogue
        navContainer.getChildren().add(createSectionLabel("Catalogue"));
        navContainer.getChildren().add(createNavButton("📁", "Catégories", "categoriesList"));
        navContainer.getChildren().add(createNavButton("📦", "Produits", "productsList"));

        // Clients
        navContainer.getChildren().add(createSectionLabel("Clients"));
        navContainer.getChildren().add(createNavButton("👥", "Clients", "clientsList"));

        // Rapports
        navContainer.getChildren().add(createSectionLabel("Rapports"));
        navContainer.getChildren().add(createNavButton("📊", "Rapports", "reports"));

        // Compte
        navContainer.getChildren().add(createSectionLabel("Compte"));
        navContainer.getChildren().add(createNavButton("🚪", "Déconnexion", "login"));

        Region filler = new Region();
        VBox.setVgrow(filler, Priority.ALWAYS);
        navContainer.getChildren().add(filler);

        contentBox.getChildren().addAll(logoArea, navContainer);
        getChildren().add(contentBox);
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-transform: uppercase;");
        label.setPadding(new Insets(12, 0, 5, 20));
        return label;
    }

    private Button createNavButton(String icon, String text, String pageId) {
        Button btn = new Button();
        btn.setUserData(pageId);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 15, 10, 20));
        btn.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-text-fill: #495057; " +
            "-fx-background-radius: 0; " +
            "-fx-border-color: transparent; " +
            "-fx-border-width: 0 0 0 3px; " +
            "-fx-cursor: hand;"
        );

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 13px;");
        content.getChildren().addAll(iconLabel, textLabel);
        btn.setGraphic(content);

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("-fx-border-color: #FF8C00")) {
                btn.setStyle(
                    "-fx-background-color: rgba(255, 165, 0, 0.08); " +
                    "-fx-text-fill: #FF8C00; " +
                    "-fx-background-radius: 0; " +
                    "-fx-border-color: rgba(255, 140, 0, 0.5); " +
                    "-fx-border-width: 0 0 0 3px; " +
                    "-fx-cursor: hand;"
                );
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FF8C00;");
                textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #FF8C00;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("-fx-border-color: #FF8C00")) {
                btn.setStyle(
                    "-fx-background-color: #f8f9fa; " +
                    "-fx-text-fill: #495057; " +
                    "-fx-background-radius: 0; " +
                    "-fx-border-color: transparent; " +
                    "-fx-border-width: 0 0 0 3px; " +
                    "-fx-cursor: hand;"
                );
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #495057;");
                textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #495057;");
            }
        });

        btn.setOnAction(e -> {
            setActiveButton(pageId);
            navigationManager.navigateTo(pageId);
        });

        menuButtons.put(pageId, btn);
        return btn;
    }

    private void setActiveButton(String pageId) {
        for (Map.Entry<String, Button> entry : menuButtons.entrySet()) {
            Button btn = entry.getValue();
            HBox content = (HBox) btn.getGraphic();
            Label iconLabel = (Label) content.getChildren().get(0);
            Label textLabel = (Label) content.getChildren().get(1);

            if (entry.getKey().equals(pageId)) {
                btn.setStyle(
                    "-fx-background-color: rgba(255, 165, 0, 0.15); " +
                    "-fx-text-fill: #FF8C00; " +
                    "-fx-background-radius: 0; " +
                    "-fx-border-color: #FF8C00; " +
                    "-fx-border-width: 0 0 0 3px; " +
                    "-fx-font-weight: 500; " +
                    "-fx-cursor: hand;"
                );
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FF8C00;");
                textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #FF8C00; -fx-font-weight: 500;");
            } else {
                btn.setStyle(
                    "-fx-background-color: #f8f9fa; " +
                    "-fx-text-fill: #495057; " +
                    "-fx-background-radius: 0; " +
                    "-fx-border-color: transparent; " +
                    "-fx-border-width: 0 0 0 3px; " +
                    "-fx-cursor: hand;"
                );
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #495057;");
                textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #495057;");
            }
        }
    }

    public void toggleCollapse() {
        isCollapsed = !isCollapsed;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), this);

        if (isCollapsed) {
            setPrefWidth(collapsedWidth);
            setMinWidth(collapsedWidth);
            setMaxWidth(collapsedWidth);
            for (Button btn : menuButtons.values()) {
                HBox content = (HBox) btn.getGraphic();
                if (content.getChildren().size() > 1) {
                    Label textLabel = (Label) content.getChildren().get(1);
                    textLabel.setVisible(false);
                }
                btn.setAlignment(Pos.CENTER);
                ((HBox) btn.getGraphic()).setAlignment(Pos.CENTER);
                ((HBox) btn.getGraphic()).setSpacing(0);
            }
            if (logoFullBox != null) logoFullBox.setVisible(false);
        } else {
            setPrefWidth(expandedWidth);
            setMinWidth(expandedWidth);
            setMaxWidth(expandedWidth);
            for (Button btn : menuButtons.values()) {
                HBox content = (HBox) btn.getGraphic();
                if (content.getChildren().size() > 1) {
                    Label textLabel = (Label) content.getChildren().get(1);
                    textLabel.setVisible(true);
                }
                btn.setAlignment(Pos.CENTER_LEFT);
                ((HBox) btn.getGraphic()).setAlignment(Pos.CENTER_LEFT);
                ((HBox) btn.getGraphic()).setSpacing(10);
            }
            if (logoFullBox != null) logoFullBox.setVisible(true);
        }
        tt.play();
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCurrentPage(String pageId) {
        currentPage = pageId;
        setActiveButton(pageId);
    }
}