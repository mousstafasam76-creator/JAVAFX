package com.inapp.view.front.Produit;

import javafx.collections.ObservableList;
import javafx.scene.control.*;

public class DeleteProductDialog extends Dialog<Boolean> {

    public DeleteProductDialog(Product product, ObservableList<Product> products, Runnable onUpdate) {
        setTitle("Supprimer le produit");
        setHeaderText("Confirmation de suppression");

        ButtonType confirmButtonType = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        Label message = new Label("Êtes-vous sûr de vouloir supprimer le produit \"" + product.getName() + "\" ?");
        message.setWrapText(true);
        getDialogPane().setContent(message);

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