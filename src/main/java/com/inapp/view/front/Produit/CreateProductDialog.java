package com.inapp.view.front.Produit;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import com.inapp.model.Product;

public class CreateProductDialog extends Dialog<Product> {

    private TextField nameField;
    private TextField descField;
    private TextField priceField;
    private TextField stockField;
    private ComboBox<String> categoryCombo;

    public CreateProductDialog(ObservableList<Product> products, Runnable onUpdate) {
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
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Électroménager", "Climatisation", "Cuisine", "Électronique");
        categoryCombo.setValue("Électroménager");

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

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(descField, Priority.ALWAYS);
        GridPane.setHgrow(priceField, Priority.ALWAYS);
        GridPane.setHgrow(stockField, Priority.ALWAYS);

        getDialogPane().setContent(grid);

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
                        
                        Product product = new Product(id, name, desc, price, stock, category, "Admin", 
                            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        products.add(product);
                        onUpdate.run();
                        return product;
                    }
                } catch (NumberFormatException e) {
                    showError("Veuillez entrer des valeurs numériques valides pour le prix et la quantité.");
                }
            }
            return null;
        });
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