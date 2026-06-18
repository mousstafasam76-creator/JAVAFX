package com.inapp.view.front.Produit;

import com.inapp.controller.front.ProduitController;
import com.inapp.model.Product;
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

public class EditProductDialog extends Dialog<Void> {

    private Map<String, Integer> categoriesMap = new LinkedHashMap<>();

    public EditProductDialog(Product product, ProduitController controller) {
        setTitle("Modifier le produit");
        setHeaderText("Modifier : " + product.getNomp());

        getDialogPane().setPrefWidth(500);
        getDialogPane().setMinWidth(500);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25));

        TextField nameField = new TextField(product.getNomp());
        nameField.setPrefWidth(300);

        TextArea descArea = new TextArea(product.getDescription());
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);

        TextField priceField = new TextField(String.valueOf(product.getPrix()));
        priceField.setPrefWidth(300);

        TextField stockField = new TextField(String.valueOf(product.getQuantite()));
        stockField.setPrefWidth(300);

        loadCategories();
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(categoriesMap.keySet());
        categoryCombo.setValue(product.getCategorieNom());

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

                    product.setNomp(name);
                    product.setDescription(desc);
                    product.setPrix(price);
                    product.setQuantite(stock);
                    product.setCategorieId(categoriesMap.getOrDefault(category, 1));
                    product.setCategorieNom(category);

                    if (controller.updateProduct(product)) {
                        showInfo("Produit modifi\u00E9 avec succ\u00E8s !");
                    } else {
                        showError("Erreur lors de la modification du produit.");
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