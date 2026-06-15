package com.inapp.view.front.commande;

import javafx.scene.layout.VBox;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;

public class UpdateStatus extends VBox {
    
    private NavigationManager navigationManager;
    private int commandeId;
    private String newStatus;
    
    public UpdateStatus(NavigationManager navManager, int id, String status) {
        this.navigationManager = navManager;
        this.commandeId = id;
        this.newStatus = status;
        updateStatus();
    }
    
    private void updateStatus() {
        String message = "";
        switch (newStatus) {
            case "livree":
                message = "Commande marquée comme livrée";
                break;
            case "annulee":
                message = "Commande annulée";
                break;
            case "en_attente":
                message = "Commande repassée en attente";
                break;
        }
        
        AlertUtils.showSuccessMessage(message);
        
        if ("livree".equals(newStatus)) {
            navigationManager.navigateTo("commandeChooseFacture?commande_id=" + commandeId);
        } else {
            navigationManager.navigateTo("commandeDetails?id=" + commandeId);
        }
    }
}