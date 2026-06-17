package com.inapp.view.front.Produit;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.inapp.model.Product;

public class DeleteProductDialog extends Dialog<Boolean> {

    public DeleteProductDialog(Product product, ObservableList<Product> products, Runnable onUpdate) {
        setTitle("Supprimer le produit");
        setHeaderText(null);

        ButtonType confirmButtonType = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label iconLabel = new Label("⚠️");
        iconLabel.setStyle("-fx-font-size: 48px;");

        Label message = new Label("Êtes-vous sûr de vouloir supprimer le produit ?");
        message.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label productName = new Label("\"" + product.getName() + "\"");
        productName.setStyle("-fx-text-fill: #E66239; -fx-font-size: 16px;");

        Label warning = new Label("Cette action est irréversible.");
        warning.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");

        content.getChildren().addAll(iconLabel, message, productName, warning);
        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                products.remove(product);
                onUpdate.run();
                return true;
            }
            return false;
        });
    }
}