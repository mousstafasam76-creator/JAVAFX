package com.inapp.controller.front;

import com.inapp.model.Commande;
import com.inapp.service.CommandeService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CommandeController {
    
    private CommandeService commandeService;
    
    public CommandeController() {
        this.commandeService = new CommandeService();
    }
    
    public List<Commande> getAllCommandes() {
        return commandeService.getAllCommandes();
    }
    
    public Map<String, Integer> getStats() {
        return commandeService.getStats();
    }
    
    public Commande getCommandeById(int id) {
        return commandeService.getCommandeById(id);
    }
    
    public List<Map<String, Object>> getCommandeDetails(int commandeId) {
        return commandeService.getCommandeDetails(commandeId);
    }
    
    public List<com.inapp.model.Client> getAllClients() {
        return commandeService.getAllClients();
    }
    
    public List<com.inapp.model.Produit> getAllProduits() {
        return commandeService.getAllProduits();
    }
    
    public int addCommande(int clientId, int userId, LocalDate dateCommande, List<Map<String, Integer>> items) throws SQLException {
        return commandeService.addCommande(clientId, userId, dateCommande, items);
    }
    
    public void updateCommande(int commandeId, int clientId, LocalDate dateCommande, 
                               List<Map<String, Integer>> items, List<Map<String, Object>> oldDetails) throws SQLException {
        commandeService.updateCommande(commandeId, clientId, dateCommande, items, oldDetails);
    }
    
    public void updateStatut(int commandeId, String newStatus) throws SQLException {
        commandeService.updateStatut(commandeId, newStatus);
    }
    
    public void deleteCommande(int commandeId) throws SQLException {
        commandeService.deleteCommande(commandeId);
    }
}