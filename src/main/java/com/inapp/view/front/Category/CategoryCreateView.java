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

public class CategoryCreateView extends VBox {

    private Dashboard dashboard;
    private TextField nameField;
    private TextArea descArea;
    private ImageView previewImage;
    private File selectedImageFile;

    public CategoryCreateView(Dashboard dashboard) {
        this.dashboard = dashboard;
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f7fa;");

        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30;");
        formCard.setMaxWidth(700);
        formCard.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Créer une nouvelle catégorie");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label pendingLabel = new Label("⏳ En attente de validation");
        pendingLabel.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 13px;");

        // Nom
        Label nameLabel = new Label("Nom de la catégorie *");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameField = new TextField();
        nameField.setPromptText("Ex: Smartphones, Ordinateurs...");
        nameField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        // Description
        Label descLabel = new Label("Description");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        descArea = new TextArea();
        descArea.setPromptText("Description courte...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        // Image
        Label imageLabel = new Label("Image de la catégorie");
        imageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        HBox imageBox = new HBox(15);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        Button uploadBtn = new Button("Choisir une image");
        uploadBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 15; -fx-cursor: hand;");
        uploadBtn.setOnAction(e -> uploadImage());

        previewImage = new ImageView();
        previewImage.setFitHeight(100);
        previewImage.setFitWidth(100);
        previewImage.setPreserveRatio(true);
        previewImage.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        imageBox.getChildren().addAll(uploadBtn, previewImage);

        // Note info
        Label infoLabel = new Label("ℹ️ Après création, un Super Admin doit valider votre catégorie.");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-padding: 10 0;");

        // Boutons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Soumettre pour validation");
        submitBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> createCategory());

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 25; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dashboard.showCategoryIndex());

        buttonsBox.getChildren().addAll(submitBtn, cancelBtn);

        formCard.getChildren().addAll(title, pendingLabel, nameLabel, nameField, descLabel, descArea,
                imageLabel, imageBox, infoLabel, buttonsBox);
        getChildren().add(formCard);
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
        }
    }

    private void createCategory() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Le nom de la catégorie est obligatoire.");
            return;
        }
        // Ici, appeler le service pour enregistrer (status = 'pending')
        // Pour l'exemple, on affiche un message et on retourne à l'index
        showToast("Catégorie créée avec succès ! En attente de validation.");
        dashboard.showCategoryIndex();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showToast(String msg) {
        // Implémentez un toast si vous voulez
        System.out.println("Toast: " + msg);
    }
}