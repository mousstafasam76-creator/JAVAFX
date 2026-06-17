package com.inapp.view.front.Produit;

import com.inapp.utils.NavigationManager;
import javafx.scene.layout.BorderPane;

public class ProductPage extends BorderPane {
    
    public ProductPage(NavigationManager navigationManager) {
        ProductViewApp productView = new ProductViewApp();
        // Supprimer la ligne qui appelle initializeAsComponent()
        this.setCenter(productView.getView());
    }
}