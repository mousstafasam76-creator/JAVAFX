package com.inapp.controller.front;

import com.inapp.model.Facture;
import com.inapp.service.FactureService;
import com.inapp.view.front.facture.FactureDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FactureController {
    private FactureService service = new FactureService();
    private ObservableList<Facture> allFactures = FXCollections.observableArrayList();
    private ObservableList<Facture> filteredFactures = FXCollections.observableArrayList();
    
    // Mémorisation des filtres actuels pour réapplication après ajout/modification
    private String currentStatusFilter = "all";
    private String currentSearch = "";
    private String currentDateFilter = "";

    public FactureController() {
        allFactures.setAll(service.getAllFactures());
        filteredFactures.setAll(allFactures);
    }

    public ObservableList<Facture> getFilteredFactures() {
        return filteredFactures;
    }

    public void applyFilter(String statusFilter, String searchText, String dateFilter) {
        this.currentStatusFilter = statusFilter;
        this.currentSearch = searchText;
        this.currentDateFilter = dateFilter;
        
        filteredFactures.clear();
        for (Facture f : allFactures) {
            // Filtre statut
            if (statusFilter.equals("paid") && !f.isPayee()) continue;
            if (statusFilter.equals("unpaid") && f.isPayee()) continue;
            // Filtre texte (numéro facture ou client)
            if (!searchText.isEmpty()) {
                String lower = searchText.toLowerCase();
                if (!f.getNomf().toLowerCase().contains(lower) &&
                    !f.getClients().toLowerCase().contains(lower)) continue;
            }
            // Filtre date
            if (!dateFilter.isEmpty()) {
                String factureDate = f.getDatef().toLocalDate().toString();
                if (!factureDate.equals(dateFilter)) continue;
            }
            filteredFactures.add(f);
        }
    }

    public void markAsPaid(Facture f) {
        allFactures.remove(f);
        Facture paid = new Facture(f.getId(), f.getNomf(), f.getDatef(), 1,
                                   f.getNbCommandes(), f.getMontantTotal(), f.getClients());
        allFactures.add(paid);
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    public void deleteFacture(Facture f) {
        allFactures.remove(f);
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    public void markMultipleAsPaid(ObservableList<Facture> selection) {
        for (Facture f : selection) {
            allFactures.remove(f);
            Facture paid = new Facture(f.getId(), f.getNomf(), f.getDatef(), 1,
                                       f.getNbCommandes(), f.getMontantTotal(), f.getClients());
            allFactures.add(paid);
        }
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    public void deleteMultiple(ObservableList<Facture> selection) {
        allFactures.removeAll(selection);
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    // Ajout d'une facture (mock)
    public void addFacture(Facture f) {
        int newId = allFactures.stream().mapToInt(Facture::getId).max().orElse(0) + 1;
        Facture withId = new Facture(newId, f.getNomf(), f.getDatef(), f.getEtatf(),
                                     0, 0.0, f.getClients());
        allFactures.add(withId);
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
        // Rendre l'ID accessible pour la navigation (sera utilisé par la vue détail)
        FactureDetail.setFactureId(newId);
    }

    // Modification d'une facture (mock)
    public void updateFacture(Facture updated) {
        for (int i = 0; i < allFactures.size(); i++) {
            if (allFactures.get(i).getId() == updated.getId()) {
                allFactures.set(i, updated);
                break;
            }
        }
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    // Statistiques
    public int getTotal() { return service.getTotal(); }
    public int getPayees() { return service.getPayees(); }
    public int getImpayees() { return service.getImpayees(); }
    public double getTotalImpaye() { return service.getTotalImpaye(); }
}