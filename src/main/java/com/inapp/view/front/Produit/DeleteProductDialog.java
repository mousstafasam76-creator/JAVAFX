package com.inapp.view.front.Produit;

import com.inapp.controller.front.ProduitController;
import com.inapp.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DeleteProductDialog extends Dialog<Boolean> {

    public DeleteProductDialog(Product product, ProduitController controller) {
        setTitle("Supprimer le produit");
        setHeaderText("Confirmation de suppression");

        getDialogPane().setPrefWidth(450);
        getDialogPane().setMinWidth(450);
        getDialogPane().setPrefHeight(200);
        getDialogPane().setMinHeight(200);

        ButtonType confirmButtonType = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);

        Label warningIcon = new Label("\u26A0\uFE0F");
        warningIcon.setStyle("-fx-font-size: 40px;");

        Label message = new Label("\u00CAtes-vous s\u00FBr de vouloir supprimer le produit \"" + product.getNomp() + "\" ?");
        message.setWrapText(true);
        message.setFont(Font.font("System", FontWeight.BOLD, 14));
        message.setAlignment(Pos.CENTER);

        Label subMessage = new Label("Cette action est irr\u00E9versible.");
        subMessage.setWrapText(true);
        subMessage.setFont(Font.font("System", 12));
        subMessage.setStyle("-fx-text-fill: #dc3545;");
        subMessage.setAlignment(Pos.CENTER);

        content.getChildren().addAll(warningIcon, message, subMessage);
        getDialogPane().setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                if (controller.deleteProduct(product.getId())) {
                    showInfo("Produit supprim\u00E9 avec succ\u00E8s !");
                    return true;
                } else {
                    showError("Erreur lors de la suppression du produit.");
                    return false;
                }
            }
            return false;
        });
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