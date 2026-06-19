package com.inapp.controller.front;

import com.inapp.model.Commande;
import com.inapp.model.Client;
import com.inapp.model.Produit;
import com.inapp.service.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Map;

public class CommandeController {
    
    private static CommandeController instance;
    private CommandeService commandeService;
    private ObservableList<Commande> commandes;
    
    private CommandeController() {
        commandeService = new CommandeService();
        commandes = FXCollections.observableArrayList();
        loadCommandes();
    }
    
    public static CommandeController getInstance() {
        if (instance == null) {
            instance = new CommandeController();
        }
        return instance;
    }
    
    public void loadCommandes() {
        commandes.setAll(commandeService.getAllCommandes());
    }
    
    public ObservableList<Commande> getCommandes() {
        return commandes;
    }
    
    public Commande findById(int id) {
        return commandes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public void addCommande(Commande commande, List<Map<String, Object>> details) {
        commandeService.addCommande(commande, details);
        loadCommandes();
    }
    
    public void updateCommande(Commande commande, List<Map<String, Object>> details) {
        commandeService.updateCommande(commande, details);
        loadCommandes();
    }
    
    public void deleteCommande(int id) {
        commandeService.deleteCommande(id);
        loadCommandes();
    }
    
    public void updateStatut(int commandeId, String statut) {
        commandeService.updateStatut(commandeId, statut);
        loadCommandes();
    }
    
    public List<Client> getAllClients() {
        return commandeService.getAllClients();
    }
    
    public List<Produit> getAllProduits() {
        return commandeService.getAllProduits();
    }
    
    public Map<String, Integer> getStats() {
        return commandeService.getStats();
    }
    
    public void refresh() {
        loadCommandes();
    }
}