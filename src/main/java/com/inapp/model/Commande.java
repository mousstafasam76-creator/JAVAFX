package com.inapp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Commande {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty clientId = new SimpleIntegerProperty();
    private final StringProperty clientName = new SimpleStringProperty();
    private final StringProperty clientTel = new SimpleStringProperty();
    private final IntegerProperty factureId = new SimpleIntegerProperty();
    private final IntegerProperty produitId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> dateCommande = new SimpleObjectProperty<>();
    private final StringProperty statut = new SimpleStringProperty();
    private final IntegerProperty totalTtc = new SimpleIntegerProperty();
    private final IntegerProperty createdBy = new SimpleIntegerProperty();
    private final StringProperty produits = new SimpleStringProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    
    public Commande() {}
    
    public Commande(int id, int clientId, String clientName, String clientTel, int factureId, 
                    int produitId, LocalDate dateCommande, String statut, int totalTtc, 
                    int createdBy, String produits) {
        setId(id);
        setClientId(clientId);
        setClientName(clientName);
        setClientTel(clientTel);
        setFactureId(factureId);
        setProduitId(produitId);
        setDateCommande(dateCommande);
        setStatut(statut);
        setTotalTtc(totalTtc);
        setCreatedBy(createdBy);
        setProduits(produits);
    }
    
    // Constructeur simplifié pour la liste
    public Commande(int id, String clientName, LocalDate dateCommande, String statut, int totalTtc, String produits) {
        setId(id);
        setClientName(clientName);
        setDateCommande(dateCommande);
        setStatut(statut);
        setTotalTtc(totalTtc);
        setProduits(produits);
    }
    
    // Constructeur pour les détails
    public Commande(int id, int clientId, String clientName, String clientTel, LocalDate dateCommande, String statut, int totalTtc) {
        setId(id);
        setClientId(clientId);
        setClientName(clientName);
        setClientTel(clientTel);
        setDateCommande(dateCommande);
        setStatut(statut);
        setTotalTtc(totalTtc);
    }
    
    // ========== GETTERS ==========
    public int getId() { return id.get(); }
    public int getClientId() { return clientId.get(); }
    public String getClientName() { return clientName.get(); }
    public String getClientTel() { return clientTel.get(); }
    public int getFactureId() { return factureId.get(); }
    public int getProduitId() { return produitId.get(); }
    public LocalDate getDateCommande() { return dateCommande.get(); }
    public String getStatut() { return statut.get(); }
    public int getTotalTtc() { return totalTtc.get(); }
    public int getCreatedBy() { return createdBy.get(); }
    public String getProduits() { return produits.get(); }
    public boolean isSelected() { return selected.get(); }
    
    // ========== SETTERS ==========
    public void setId(int v) { id.set(v); }
    public void setClientId(int v) { clientId.set(v); }
    public void setClientName(String v) { clientName.set(v); }
    public void setClientTel(String v) { clientTel.set(v); }
    public void setFactureId(int v) { factureId.set(v); }
    public void setProduitId(int v) { produitId.set(v); }
    public void setDateCommande(LocalDate v) { dateCommande.set(v); }
    public void setStatut(String v) { statut.set(v); }
    public void setTotalTtc(int v) { totalTtc.set(v); }
    public void setCreatedBy(int v) { createdBy.set(v); }
    public void setProduits(String v) { produits.set(v); }
    public void setSelected(boolean v) { selected.set(v); }
    
    // ========== PROPERTIES ==========
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty clientIdProperty() { return clientId; }
    public StringProperty clientNameProperty() { return clientName; }
    public StringProperty clientTelProperty() { return clientTel; }
    public IntegerProperty factureIdProperty() { return factureId; }
    public IntegerProperty produitIdProperty() { return produitId; }
    public ObjectProperty<LocalDate> dateCommandeProperty() { return dateCommande; }
    public StringProperty statutProperty() { return statut; }
    public IntegerProperty totalTtcProperty() { return totalTtc; }
    public IntegerProperty createdByProperty() { return createdBy; }
    public StringProperty produitsProperty() { return produits; }
    public BooleanProperty selectedProperty() { return selected; }
    
    @Override
    public String toString() {
        return "Commande #" + getId() + " - " + getClientName();
    }
}