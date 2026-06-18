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
import java.io.File;
import javafx.stage.FileChooser;

public class CategoryEditView extends VBox {

    private Dashboard dashboard;
    private int categoryId;
    private com.inapp.model.Category category;
    private TextField nameField;
    private TextArea descArea;
    private ImageView previewImage;
    private File selectedImageFile;
    private String currentImagePath;
    private CheckBox deleteImageCheck;

    public CategoryEditView(Dashboard dashboard, int categoryId) {
        this.dashboard = dashboard;
        this.categoryId = categoryId;
        // Charger la catégorie depuis un service (ici, exemple fictif)
        chargerCategorie();

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f7fa;");

        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30;");
        formCard.setMaxWidth(700);
        formCard.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Modifier la catégorie");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Statut
        Label statusLabel = new Label();
        if ("pending".equals(category.getStatus()))
            statusLabel.setText("⏳ En attente de validation");
        else if ("approved".equals(category.getStatus()))
            statusLabel.setText("✅ Déjà validée (modifications limitées)");
        else
            statusLabel.setText("❌ Rejetée - après modification, sera re-soumise");
        statusLabel.setStyle("-fx-background-color: #e9ecef; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 13px;");

        // Nom
        Label nameLabel = new Label("Nom de la catégorie *");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameField = new TextField(category.getName());
        nameField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        // Description
        Label descLabel = new Label("Description");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        descArea = new TextArea(category.getDescription());
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        // Image
        Label imageLabel = new Label("Image de la catégorie");
        imageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Image actuelle
        currentImagePath = category.getImageUrl(); // supposé
        previewImage = new ImageView();
        previewImage.setFitHeight(100);
        previewImage.setFitWidth(100);
        previewImage.setPreserveRatio(true);
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            previewImage.setImage(new Image(currentImagePath));
        }
        previewImage.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        deleteImageCheck = new CheckBox("Supprimer l'image actuelle");
        deleteImageCheck.setStyle("-fx-font-size: 12px;");

        HBox imageBox = new HBox(15);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        Button uploadBtn = new Button("Changer l'image");
        uploadBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 15; -fx-cursor: hand;");
        uploadBtn.setOnAction(e -> uploadImage());

        imageBox.getChildren().addAll(uploadBtn, previewImage);

        // Boutons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Enregistrer les modifications");
        submitBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> updateCategory());

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dashboard.showCategoryIndex());

        buttonsBox.getChildren().addAll(submitBtn, cancelBtn);

        formCard.getChildren().addAll(title, statusLabel, nameLabel, nameField, descLabel, descArea,
                imageLabel, imageBox, deleteImageCheck, buttonsBox);
        getChildren().add(formCard);
    }

    private void chargerCategorie() {
        // Simuler le chargement (remplacer par appel à CategoryService)
        category = new com.inapp.model.Category(categoryId, "Exemple", "Description", "rejected");
        category.setImageUrl("/images/product-1.png");
    }

    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.webp")
        );
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            previewImage.setImage(new Image(file.toURI().toString()));
            deleteImageCheck.setSelected(false);
        }
    }

    private void updateCategory() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Le nom est obligatoire.");
            return;
        }
        category.setName(name);
        category.setDescription(descArea.getText());
        // Si le statut était 'rejected', on repasse en 'pending'
        if ("rejected".equals(category.getStatus())) {
            category.setStatus("pending");
        }
        // Gestion de l'image
        if (deleteImageCheck.isSelected()) {
            // Supprimer l'image
            category.setImageUrl(null);
        } else if (selectedImageFile != null) {
            // Ici, uploader le fichier et définir l'URL
            category.setImageUrl("/images/uploaded/" + selectedImageFile.getName());
        }
        // Appeler service.update(category)
        showToast("Catégorie modifiée avec succès !");
        dashboard.showCategoryIndex();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showToast(String msg) {
        System.out.println("Toast: " + msg);
    }
}