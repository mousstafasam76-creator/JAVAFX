package com.inapp.controller.front;

import com.inapp.model.Client;
import com.inapp.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientController {
    
    private static ClientController instance;
    private ClientService clientService;
    private ObservableList<Client> clients;
    
    private ClientController() {
        clientService = new ClientService();
        clients = FXCollections.observableArrayList();
        loadClients();
    }
    
    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }
    
    public void loadClients() {
        clients.setAll(clientService.getAllClients());
        // Mettre à jour les statistiques pour chaque client
        for (Client client : clients) {
            int nbCmd = clientService.getNbCommandes(client.getId());
            double totalAchats = clientService.getTotalAchats(client.getId());
            client.setNbCommandes(nbCmd);
            client.setTotalAchats(totalAchats);
        }
    }
    
    public ObservableList<Client> getClients() {
        return clients;
    }
    
    public Client findById(int id) {
        return clients.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public void addClient(Client client) {
        clientService.addClient(client);
        // Recharger pour avoir l'ID généré
        loadClients();
    }
    
    public void updateClient(Client client) {
        clientService.updateClient(client);
        loadClients();
    }
    
    public void deleteClient(Client client) {
        clientService.deleteClient(client);
        clients.remove(client);
    }
    
    public void refresh() {
        loadClients();
    }
}