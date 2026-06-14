package com.inapp.view.front.Produit;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EditProductDialog extends Dialog<Product> {

    public EditProductDialog(Product product, Runnable onUpdate) {
        setTitle("Modifier le produit");
        setHeaderText("Modifier: " + product.getName());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(product.getName());
        TextField descField = new TextField(product.getDescription());
        TextField priceField = new TextField(String.valueOf((int) product.getPrice()));
        TextField stockField = new TextField(String.valueOf(product.getStock()));
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Électroménager", "Climatisation", "Cuisine", "Électronique");
        categoryCombo.setValue(product.getCategory());

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
                    product.setName(nameField.getText());
                    product.setDescription(descField.getText());
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setStock(Integer.parseInt(stockField.getText()));
                    product.setCategory(categoryCombo.getValue());
                    onUpdate.run();
                    return product;
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Valeurs numériques invalides.");
                    alert.showAndWait();
                }
            }
            return null;
        });
    }
}