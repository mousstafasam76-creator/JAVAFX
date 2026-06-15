package com.inapp.view.front.commande;

import javafx.scene.layout.VBox;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;

public class ProcessLivraison extends VBox {
    
    private NavigationManager navigationManager;
    private int commandeId;
    private String option;
    private int factureId;
    
    public ProcessLivraison(NavigationManager navManager, int commandeId, String option, int factureId) {
        this.navigationManager = navManager;
        this.commandeId = commandeId;
        this.option = option;
        this.factureId = factureId;
        process();
    }
    
    private void process() {
        String message = "";
        switch (option) {
            case "existing":
                message = "Commande ajoutée à la facture #" + factureId + " et marquée comme livrée";
                break;
            case "new":
                message = "Nouvelle facture créée et commande marquée comme livrée";
                break;
            case "none":
                message = "Commande marquée comme livrée sans facture";
                break;
        }
        
        AlertUtils.showSuccessMessage(message);
        navigationManager.navigateTo("commandeDetails?id=" + commandeId);
    }
}