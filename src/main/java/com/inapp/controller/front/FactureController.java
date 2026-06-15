 package com.inapp.controller.front;

import com.inapp.model.Facture;
import com.inapp.service.FactureService;
import com.inapp.view.front.facture.FactureDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;

public class FactureController {
    private FactureService service = new FactureService();
    private ObservableList<Facture> allFactures = FXCollections.observableArrayList();
    private ObservableList<Facture> filteredFactures = FXCollections.observableArrayList();
    
    private String currentStatusFilter = "all";
    private String currentSearch = "";
    private String currentDateFilter = "";

    public FactureController() {
        refreshFromDatabase();
    }

    private void refreshFromDatabase() {
        allFactures.setAll(service.getAllFactures());
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
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
            if (statusFilter.equals("paid") && !f.isPayee()) continue;
            if (statusFilter.equals("unpaid") && f.isPayee()) continue;
            if (!searchText.isEmpty()) {
                String lower = searchText.toLowerCase();
                if (!f.getNomf().toLowerCase().contains(lower) &&
                    !f.getClients().toLowerCase().contains(lower)) continue;
            }
            if (!dateFilter.isEmpty()) {
                String factureDate = f.getDatef().toLocalDate().toString();
                if (!factureDate.equals(dateFilter)) continue;
            }
            filteredFactures.add(f);
        }
    }

    // CRUD Operations
    public Facture addFacture(Facture f) {
        Facture saved = service.addFacture(f);
        if (saved != null) {
            allFactures.add(saved);
            applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
            FactureDetail.setFactureId(saved.getId());
        }
        return saved;
    }

    public void updateFacture(Facture updated) {
        service.updateFacture(updated);
        for (int i = 0; i < allFactures.size(); i++) {
            if (allFactures.get(i).getId() == updated.getId()) {
                allFactures.set(i, updated);
                break;
            }
        }
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    public void markAsPaid(Facture f) {
        service.markAsPaid(f.getId());
        refreshFromDatabase();
    }

    public void deleteFacture(Facture f) {
        service.deleteFacture(f.getId());
        allFactures.remove(f);
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    public void markMultipleAsPaid(ObservableList<Facture> selection) {
        service.markMultipleAsPaid(selection.stream().map(Facture::getId).collect(Collectors.toList()));
        refreshFromDatabase();
    }

    public void deleteMultiple(ObservableList<Facture> selection) {
        service.deleteMultiple(selection.stream().map(Facture::getId).collect(Collectors.toList()));
        allFactures.removeAll(selection);
        applyFilter(currentStatusFilter, currentSearch, currentDateFilter);
    }

    // Statistiques
    public int getTotal() { return allFactures.size(); }
    public int getPayees() { return (int) allFactures.stream().filter(Facture::isPayee).count(); }
    public int getImpayees() { return getTotal() - getPayees(); }
    public double getTotalImpaye() {
        return allFactures.stream().filter(f -> !f.isPayee()).mapToDouble(Facture::getMontantTotal).sum();
    }
}