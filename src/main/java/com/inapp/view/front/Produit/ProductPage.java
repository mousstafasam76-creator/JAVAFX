package com.inapp.view.front.Produit;

import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Sidebar;
import javafx.scene.layout.BorderPane;

public class ProductPage extends BorderPane {
    
    private Sidebar sidebar;
    
    public ProductPage(NavigationManager navigationManager) {
        sidebar = new Sidebar(navigationManager);
        sidebar.setCurrentPage("products");
        this.setLeft(sidebar);
        ProductViewApp productView = new ProductViewApp();
        productView.initializeAsComponent();
        this.setCenter(productView.getView());
    }
}