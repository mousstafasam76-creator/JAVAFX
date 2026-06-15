package com.inapp.view.front.commande;

import javafx.scene.layout.VBox;
import com.inapp.utils.NavigationManager;

public class GetStats extends VBox {
    
    private NavigationManager navigationManager;
    
    public GetStats(NavigationManager navManager) {
        this.navigationManager = navManager;
        // Les stats sont déjà gérées dans CommandeListView
        navigationManager.navigateTo("commandesList");
    }
}