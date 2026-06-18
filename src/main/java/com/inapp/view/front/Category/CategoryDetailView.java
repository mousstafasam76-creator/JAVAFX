package com.inapp.view.front.Category;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.inapp.view.front.Dashboard;
import java.util.ArrayList;
import java.util.List;

public class CategoryDetailView extends VBox {

    private Dashboard dashboard;
    private int categoryId;
    private com.inapp.model.Category category;
    private List<com.inapp.model.Product> products;

    public CategoryDetailView(Dashboard dashboard, int categoryId) {
        this.dashboard = dashboard;
        this.categoryId = categoryId;
        chargerCategorie();
        chargerProduits();

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f7fa;");

        // En-tête avec retour
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Retour aux catégories");
        backBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        backBtn.setOnAction(e -> dashboard.showCategoryIndex());

        Label title = new Label("Détails de la catégorie");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        editBtn.setOnAction(e -> dashboard.showCategoryEdit(categoryId));
        header.getChildren().addAll(backBtn, spacer, title, editBtn);

        // Informations générales
        VBox infoCard = new VBox(15);
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20;");

        // Image + nom + statut
        HBox topInfo = new HBox(20);
        topInfo.setAlignment(Pos.CENTER_LEFT);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(category.getImageUrl() != null ? category.getImageUrl() : "https://via.placeholder.com/100"));

        VBox textInfo = new VBox(5);
        Label nameLabel = new Label(category.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Label statusLabel = new Label("Statut : " + category.getStatus());
        String statusColor = "approved".equals(category.getStatus()) ? "#28a745" : "pending".equals(category.getStatus()) ? "#ffc107" : "#dc3545";
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");
        textInfo.getChildren().addAll(nameLabel, statusLabel);
        topInfo.getChildren().addAll(imageView, textInfo);

        // Grille d'informations
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);
        String[] labels = {"ID", "Créée par", "Validée par", "Date validation"};
        String[] values = {
            String.valueOf(category.getId()),
            "Admin", // à remplacer
            "SuperAdmin", // à remplacer
            "2025-01-01" // à remplacer
        };
        for (int i = 0; i < labels.length; i++) {
            Label l = new Label(labels[i]);
            l.setStyle("-fx-font-weight: bold; -fx-text-fill: #666; -fx-font-size: 12px;");
            Label v = new Label(values[i]);
            infoGrid.add(l, 0, i);
            infoGrid.add(v, 1, i);
        }

        infoCard.getChildren().addAll(topInfo, infoGrid);

        // Statistiques
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");
        double totalValue = products.stream().mapToDouble(com.inapp.model.Product::getPrice).sum();
        Label prodCount = new Label("📦 " + products.size() + " produits");
        prodCount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label valueLabel = new Label("💰 " + String.format("%,.0f", totalValue) + " FCFA");
        valueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        statsBox.getChildren().addAll(prodCount, valueLabel);

        // Liste des produits
        VBox productBox = new VBox(10);
        productBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20;");
        Label productTitle = new Label("Produits de cette catégorie");
        productTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<com.inapp.model.Product> tableView = new TableView<>();
        TableColumn<com.inapp.model.Product, Integer> idCol = new TableColumn<>("ID");
        TableColumn<com.inapp.model.Product, String> nameCol = new TableColumn<>("Nom");
        TableColumn<com.inapp.model.Product, Double> priceCol = new TableColumn<>("Prix");
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        tableView.getColumns().addAll(idCol, nameCol, priceCol);
        tableView.setItems(javafx.collections.FXCollections.observableArrayList(products));
        tableView.setPrefHeight(250);

        productBox.getChildren().addAll(productTitle, tableView);

        getChildren().addAll(header, infoCard, statsBox, productBox);
    }

    private void chargerCategorie() {
        // Simuler
        category = new com.inapp.model.Category(categoryId, "Téléphone", "Smartphones haut de gamme", "approved");
        category.setImageUrl("/images/product-1.png");
    }

    private void chargerProduits() {
        products = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            com.inapp.model.Product p = new com.inapp.model.Product();
            p.setId(i);
            p.setName("Produit " + i);
            p.setPrice(150000 * i);
            products.add(p);
        }
    }
}