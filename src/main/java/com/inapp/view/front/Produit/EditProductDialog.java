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

public class EditProductDialog extends Dialog<Product> {

    private TextField nameField;
    private TextField descField;
    private TextField priceField;
    private TextField stockField;
    private ComboBox<String> categoryCombo;
    private Label imagePathLabel;
    private File selectedImageFile;
    private String currentImagePath;
    private ProductController productController;

    public EditProductDialog(Product product, Runnable onUpdate) {
        this.productController = ProductController.getInstance();
        this.currentImagePath = product.getImageUrl();
        
        setTitle("Modifier le produit");
        setHeaderText("Modifier les informations du produit");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField = new TextField(product.getName());
        nameField.setPromptText("Nom du produit");
        descField = new TextField(product.getDescription());
        descField.setPromptText("Description");
        priceField = new TextField(String.valueOf(product.getPrice()));
        priceField.setPromptText("Prix (FCFA)");
        stockField = new TextField(String.valueOf(product.getQuantity()));
        stockField.setPromptText("Quantité");
        
        // Charger les catégories depuis la base de données
        categoryCombo = new ComboBox<>();
        ObservableList<String> categories = productController.getCategories();
        if (categories.isEmpty()) {
            categoryCombo.getItems().addAll("Électroménager", "Climatisation", "Cuisine", "Électronique");
        } else {
            categoryCombo.getItems().addAll(categories);
        }
        categoryCombo.setValue(product.getCategoryName());

        // Champ pour l'image
        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        
        Button chooseImageBtn = new Button("Choisir une nouvelle image");
        chooseImageBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        String currentImageDisplay = (currentImagePath != null && !currentImagePath.isEmpty()) 
            ? "✅ Image actuelle: " + currentImagePath.substring(currentImagePath.lastIndexOf("/") + 1) 
            : "Aucune image";
        imagePathLabel = new Label(currentImageDisplay);
        imagePathLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        
        Button removeImageBtn = new Button("🗑️ Supprimer l'image");
        removeImageBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6px 12px; -fx-background-radius: 8px; -fx-cursor: hand;");
        removeImageBtn.setOnAction(e -> {
            selectedImageFile = null;
            currentImagePath = null;
            imagePathLabel.setText("Aucune image (supprimée)");
            imagePathLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        });
        
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
        
        HBox imageButtons = new HBox(10, chooseImageBtn, removeImageBtn);
        imageBox.getChildren().addAll(imageButtons, imagePathLabel);

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

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    if (validateFields()) {
                        String name = nameField.getText().trim();
                        String desc = descField.getText().trim();
                        double price = Double.parseDouble(priceField.getText().trim());
                        int stock = Integer.parseInt(stockField.getText().trim());
                        String category = categoryCombo.getValue();
                        
                        product.setName(name);
                        product.setDescription(desc);
                        product.setPrice(price);
                        product.setQuantity(stock);
                        product.setCategoryName(category);
                        
                        String imagePath = null;
                        if (selectedImageFile != null && selectedImageFile.exists()) {
                            imagePath = saveImage(selectedImageFile, product.getId());
                        } else if (currentImagePath != null && !currentImagePath.isEmpty()) {
                            imagePath = currentImagePath;
                        }
                        
                        product.setImageUrl(imagePath);
                        
                        productController.updateProduct(product);
                        onUpdate.run();
                        
                        Alert success = new Alert(Alert.AlertType.INFORMATION, "Produit modifié avec succès !");
                        success.setHeaderText(null);
                        success.showAndWait();
                        
                        return product;
                    }
                } catch (NumberFormatException e) {
                    showError("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
                } catch (Exception e) {
                    showError("Erreur lors de la modification du produit: " + e.getMessage());
                }
            }
            return null;
        });
    }

    private String saveImage(File sourceFile, int productId) {
        try {
            String srcDir = "src/main/resources/images/products/";
            File dir = new File(srcDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
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
            
            File srcTargetFile = new File(srcDir + targetFileName);
            Files.copy(sourceFile.toPath(), srcTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Image copiée dans src: " + srcTargetFile.getAbsolutePath());
            
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