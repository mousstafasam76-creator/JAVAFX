package com.inapp.controller.front;

import com.inapp.model.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientController {

    private static ClientController instance;
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final AtomicInteger idCounter = new AtomicInteger(7);

    private ClientController() {
        // Données fictives (remplacer par DB plus tard)
        clients.addAll(
            new Client(1, "Amadou", "Coulibaly", "76-12-34-56", "amadou@mail.com", "Bamako, ACI 2000", 5, 125000),
            new Client(2, "Fatoumata", "Diallo", "79-98-76-54", "fato@mail.com", "Bamako, Badalabougou", 3, 87500),
            new Client(3, "Ibrahim", "Traoré", "78-45-67-89", "ibrahim@mail.com", "Bamako, Lafiabougou", 8, 230000),
            new Client(4, "Mariam", "Keita", "70-11-22-33", "mariam@mail.com", "Bamako, Hippodrome", 2, 45000),
            new Client(5, "Oumar", "Sanogo", "77-55-44-33", "oumar@mail.com", "Bamako, Kalaban Coura", 1, 18000),
            new Client(6, "Aissata", "Koné", "65-77-88-99", "aissata@mail.com", "Bamako, Magnambougou", 0, 0)
        );
    }

    public static ClientController getInstance() {
        if (instance == null) instance = new ClientController();
        return instance;
    }

    public ObservableList<Client> getClients() { return clients; }

    public void addClient(Client c) {
        c.setId(idCounter.getAndIncrement());
        clients.add(c);
    }

    public void updateClient(Client updated) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == updated.getId()) {
                clients.set(i, updated);
                return;
            }
        }
    }

    public void deleteClient(Client c) {
        clients.remove(c);
    }

    public Client findById(int id) {
        return clients.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
