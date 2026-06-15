package com.inapp.model;

import javafx.beans.property.*;

public class Commande {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty clientName = new SimpleStringProperty();
    private final StringProperty dateCommande = new SimpleStringProperty();
    private final StringProperty statut = new SimpleStringProperty();
    private final DoubleProperty montantTotal = new SimpleDoubleProperty();
    private final StringProperty produits = new SimpleStringProperty();

    // Constructeur par défaut
    public Commande() {}

    // Constructeur avec paramètres
    public Commande(int id, String clientName, String dateCommande, String statut, double montantTotal, String produits) {
        setId(id);
        setClientName(clientName);
        setDateCommande(dateCommande);
        setStatut(statut);
        setMontantTotal(montantTotal);
        setProduits(produits);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getClientName() { return clientName.get(); }
    public String getDateCommande() { return dateCommande.get(); }
    public String getStatut() { return statut.get(); }
    public double getMontantTotal() { return montantTotal.get(); }
    public String getProduits() { return produits.get(); }

    // Setters
    public void setId(int value) { id.set(value); }
    public void setClientName(String value) { clientName.set(value); }
    public void setDateCommande(String value) { dateCommande.set(value); }
    public void setStatut(String value) { statut.set(value); }
    public void setMontantTotal(double value) { montantTotal.set(value); }
    public void setProduits(String value) { produits.set(value); }

    // Property getters (pour PropertyValueFactory)
    public IntegerProperty idProperty() { return id; }
    public StringProperty clientNameProperty() { return clientName; }
    public StringProperty dateCommandeProperty() { return dateCommande; }
    public StringProperty statutProperty() { return statut; }
    public DoubleProperty montantTotalProperty() { return montantTotal; }
    public StringProperty produitsProperty() { return produits; }

    @Override
    public String toString() {
        return "Commande #" + getId() + " - " + getClientName() + " - " + getMontantTotal() + " FCFA";
    }
}