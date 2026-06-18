package com.inapp.view.front.Produit;

import com.inapp.controller.front.ProduitController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import com.inapp.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class CreateProductDialog extends Dialog<Void> {

    private Map<String, Integer> categoriesMap = new LinkedHashMap<>();

    public CreateProductDialog(ProduitController controller) {
        setTitle("Nouveau produit");
        setHeaderText("Cr\u00E9er un nouveau produit");

        getDialogPane().setPrefWidth(500);
        getDialogPane().setMinWidth(500);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom du produit");
        nameField.setPrefWidth(300);

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description du produit");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);

        TextField priceField = new TextField();
        priceField.setPromptText("Prix en FCFA");
        priceField.setPrefWidth(300);

        TextField stockField = new TextField();
        stockField.setPromptText("Quantit\u00E9 en stock");
        stockField.setPrefWidth(300);

        loadCategories();
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(categoriesMap.keySet());
        if (!categoriesMap.isEmpty()) {
            categoryCombo.setValue(categoriesMap.keySet().iterator().next());
        }

        Label nameLabel = new Label("Nom du produit :");
        nameLabel.setPrefWidth(130);
        Label descLabel = new Label("Description :");
        descLabel.setPrefWidth(130);
        Label priceLabel = new Label("Prix (FCFA) :");
        priceLabel.setPrefWidth(130);
        Label stockLabel = new Label("Quantit\u00E9 :");
        stockLabel.setPrefWidth(130);
        Label catLabel = new Label("Cat\u00E9gorie :");
        catLabel.setPrefWidth(130);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(priceLabel, 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(stockLabel, 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(catLabel, 0, 4);
        grid.add(categoryCombo, 1, 4);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(descArea, Priority.ALWAYS);
        GridPane.setHgrow(priceField, Priority.ALWAYS);
        GridPane.setHgrow(stockField, Priority.ALWAYS);
        GridPane.setHgrow(categoryCombo, Priority.ALWAYS);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String desc = descArea.getText().trim();
                String priceText = priceField.getText().trim();
                String stockText = stockField.getText().trim();

                if (name.isEmpty()) {
                    showError("Le nom du produit est obligatoire.");
                    return null;
                }

                try {
                    int price = Integer.parseInt(priceText);
                    int stock = Integer.parseInt(stockText);
                    String category = categoryCombo.getValue();
                    int categoryId = categoriesMap.getOrDefault(category, 1);

                    int newId = controller.createProduct(name, price, stock, desc, 1, categoryId);
                    if (newId > 0) {
                        showInfo("Produit cr\u00E9\u00E9 avec succ\u00E8s !");
                    } else {
                        showError("Erreur lors de la cr\u00E9ation du produit.");
                    }
                } catch (NumberFormatException e) {
                    showError("Veuillez entrer des valeurs num\u00E9riques valides pour le prix et la quantit\u00E9.");
                }
            }
            return null;
        });
    }

    private void loadCategories() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nomcat FROM categories ORDER BY nomcat")) {
            while (rs.next()) {
                categoriesMap.put(rs.getString("nomcat"), rs.getInt("id"));
            }
        } catch (Exception e) {
            categoriesMap.put("telephone", 1);
            categoriesMap.put("TV", 3);
            categoriesMap.put("accessoires", 4);
            categoriesMap.put("Son et audio", 5);
            categoriesMap.put("Ordinateur", 16);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succ\u00E8s");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
}