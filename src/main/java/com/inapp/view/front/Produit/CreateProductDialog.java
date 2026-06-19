package com.inapp.view.front.Produit;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.inapp.model.Product;
import com.inapp.controller.front.ProductController;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CreateProductDialog extends Dialog<Product> {

    private TextField nameField;
    private TextField descField;
    private TextField priceField;
    private TextField stockField;
    private ComboBox<String> categoryCombo;
    private Label imagePathLabel;
    private File selectedImageFile;
    private ProductController productController;

    public CreateProductDialog(ObservableList<Product> products, Runnable onUpdate) {
        this.productController = ProductController.getInstance();
        
        setTitle("Nouveau produit");
        setHeaderText("Créer un nouveau produit");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText("Nom du produit");
        descField = new TextField();
        descField.setPromptText("Description");
        priceField = new TextField();
        priceField.setPromptText("Prix (FCFA)");
        stockField = new TextField();
        stockField.setPromptText("Quantité");

        // Charger les catégories depuis la base de données
        categoryCombo = new ComboBox<>();
        ObservableList<String> categories = productController.getCategories();
        if (categories.isEmpty()) {
            categoryCombo.getItems().addAll("Électroménager", "Climatisation", "Cuisine", "Électronique");
        } else {
            categoryCombo.getItems().addAll(categories);
        }
        categoryCombo.setValue(categoryCombo.getItems().isEmpty() ? null : categoryCombo.getItems().get(0));

        // Champ pour l'image
        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        
        Button chooseImageBtn = new Button("Choisir une image");
        chooseImageBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        imagePathLabel = new Label("Aucune image sélectionnée");
        imagePathLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        chooseImageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image pour le produit");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
            File file = fileChooser.showOpenDialog(getDialogPane().getScene().getWindow());
            if (file != null) {
                selectedImageFile = file;
                imagePathLabel.setText("📷 " + file.getName() + " (sélectionnée)");
                imagePathLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });
        
        imageBox.getChildren().addAll(chooseImageBtn, imagePathLabel);

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Prix (FCFA):"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Quantité:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Catégorie:"), 0, 4);
        grid.add(categoryCombo, 1, 4);
        grid.add(new Label("Image:"), 0, 5);
        grid.add(imageBox, 1, 5);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(descField, Priority.ALWAYS);
        GridPane.setHgrow(priceField, Priority.ALWAYS);
        GridPane.setHgrow(stockField, Priority.ALWAYS);

        getDialogPane().setContent(grid);

        // Désactiver le bouton OK si les champs sont vides
        final Button okButton = (Button) getDialogPane().lookupButton(saveButtonType);
        okButton.setDisable(true);
        
        // Ajouter des écouteurs pour valider les champs
        nameField.textProperty().addListener((obs, old, val) -> validateForm(okButton));
        priceField.textProperty().addListener((obs, old, val) -> validateForm(okButton));
        stockField.textProperty().addListener((obs, old, val) -> validateForm(okButton));

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    if (validateFields()) {
                        int id = products.size() + 1;
                        String name = nameField.getText().trim();
                        String desc = descField.getText().trim();
                        double price = Double.parseDouble(priceField.getText().trim());
                        int stock = Integer.parseInt(stockField.getText().trim());
                        String category = categoryCombo.getValue();
                        
                        // Traiter l'image
                        String imagePath = null;
                        if (selectedImageFile != null && selectedImageFile.exists()) {
                            imagePath = saveImage(selectedImageFile, id);
                        }
                        
                        Product product = new Product(id, name, desc, price, stock, category, "Admin", 
                            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        
                        if (imagePath != null) {
                            product.setImageUrl(imagePath);
                        }
                        
                        products.add(product);
                        productController.addProduct(product);
                        onUpdate.run();
                        
                        Alert success = new Alert(Alert.AlertType.INFORMATION, 
                            "Produit créé avec succès !" + (imagePath != null ? "\nImage enregistrée: " + imagePath : ""));
                        success.setHeaderText(null);
                        success.showAndWait();
                        
                        return product;
                    }
                } catch (NumberFormatException e) {
                    showError("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
                } catch (Exception e) {
                    showError("Erreur lors de la création du produit: " + e.getMessage());
                }
            }
            return null;
        });
    }

    private void validateForm(Button okButton) {
        boolean hasName = !nameField.getText().trim().isEmpty();
        boolean hasPrice = !priceField.getText().trim().isEmpty();
        boolean hasStock = !stockField.getText().trim().isEmpty();
        okButton.setDisable(!(hasName && hasPrice && hasStock));
    }

    private String saveImage(File sourceFile, int productId) {
        try {
            // 1. Dossier source (pour la compilation)
            String srcDir = "src/main/resources/images/products/";
            File dir = new File(srcDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 2. Dossier out (pour l'exécution)
            String outDir = "out/images/products/";
            File outDirFile = new File(outDir);
            if (!outDirFile.exists()) {
                outDirFile.mkdirs();
            }
            
            String extension = "";
            String fileName = sourceFile.getName();
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }
            
            String targetFileName = "product-" + productId + extension;
            
            // Copier dans src (pour la prochaine compilation)
            File srcTargetFile = new File(srcDir + targetFileName);
            Files.copy(sourceFile.toPath(), srcTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Image copiée dans src: " + srcTargetFile.getAbsolutePath());
            
            // Copier dans out (pour l'exécution immédiate)
            File outTargetFile = new File(outDir + targetFileName);
            Files.copy(sourceFile.toPath(), outTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Image copiée dans out: " + outTargetFile.getAbsolutePath());
            
            return "/images/products/" + targetFileName;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la sauvegarde de l'image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Veuillez saisir un nom de produit");
            return false;
        }
        if (priceField.getText().trim().isEmpty()) {
            showError("Veuillez saisir un prix");
            return false;
        }
        if (stockField.getText().trim().isEmpty()) {
            showError("Veuillez saisir une quantité");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}