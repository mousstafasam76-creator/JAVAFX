package com.inapp.view.front.Category;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.inapp.utils.NavigationManager;
import com.inapp.model.Category;
import com.inapp.controller.front.CategoryController;
import javafx.stage.FileChooser;
import java.io.File;

public class CategoryEditView extends VBox {

    private NavigationManager navigationManager;
    private int categoryId;
    private Category category;
    private TextField nameField;
    private TextArea descArea;
    private ImageView previewImage;
    private File selectedImageFile;
    private String currentImagePath;
    private CheckBox deleteImageCheck;
    private CategoryController categoryController;

    public CategoryEditView(NavigationManager navManager, int categoryId) {
        this.navigationManager = navManager;
        this.categoryId = categoryId;
        this.categoryController = CategoryController.getInstance();
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

        Label statusLabel = new Label();
        if ("pending".equals(category.getStatus()))
            statusLabel.setText("⏳ En attente de validation");
        else if ("approved".equals(category.getStatus()))
            statusLabel.setText("✅ Déjà validée (modifications limitées)");
        else
            statusLabel.setText("❌ Rejetée - après modification, sera re-soumise");
        statusLabel.setStyle("-fx-background-color: #e9ecef; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 13px;");

        Label nameLabel = new Label("Nom de la catégorie *");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameField = new TextField(category.getName());
        nameField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        Label descLabel = new Label("Description");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        descArea = new TextArea(category.getDescription());
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        Label imageLabel = new Label("Image de la catégorie");
        imageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        currentImagePath = category.getImageUrl();
        previewImage = new ImageView();
        previewImage.setFitHeight(100);
        previewImage.setFitWidth(100);
        previewImage.setPreserveRatio(true);
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            try {
                previewImage.setImage(new Image(currentImagePath));
            } catch (Exception e) {
                previewImage.setImage(new Image("https://via.placeholder.com/100"));
            }
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

        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Enregistrer les modifications");
        submitBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> updateCategory());

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> navigationManager.navigateTo("categories"));

        buttonsBox.getChildren().addAll(submitBtn, cancelBtn);

        formCard.getChildren().addAll(title, statusLabel, nameLabel, nameField, descLabel, descArea,
                imageLabel, imageBox, deleteImageCheck, buttonsBox);
        getChildren().add(formCard);
    }

    private void chargerCategorie() {
        category = categoryController.findById(categoryId);
        if (category == null) {
            category = new Category(categoryId, "Catégorie inconnue", "", "unknown");
        }
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
        
        if ("rejected".equals(category.getStatus())) {
            category.setStatus("pending");
        }
        
        if (deleteImageCheck.isSelected()) {
            category.setImageUrl(null);
        } else if (selectedImageFile != null) {
            category.setImageUrl("/images/categories/" + selectedImageFile.getName());
        }
        
        categoryController.updateCategory(category);
        showToast("Catégorie modifiée avec succès !");
        navigationManager.navigateTo("categories");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showToast(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}