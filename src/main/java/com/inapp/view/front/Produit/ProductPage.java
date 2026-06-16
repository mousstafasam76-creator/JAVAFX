package com.inapp.view.front.Produit;

import com.inapp.utils.NavigationManager;
import javafx.scene.layout.BorderPane;

public class ProductPage extends BorderPane {
    
    public ProductPage(NavigationManager navigationManager) {
        // Le Sidebar est déjà ajouté par mainLayout dans MainApplication
        // Nous n'ajoutons pas de Sidebar ici pour éviter le double affichage
        
        ProductViewApp productView = new ProductViewApp();
        productView.initializeAsComponent();
        this.setCenter(productView.getView());
    }
}