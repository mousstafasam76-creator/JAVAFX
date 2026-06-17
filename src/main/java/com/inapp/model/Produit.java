package com.inapp.model;

import javafx.beans.property.*;

public class Produit {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nomp = new SimpleStringProperty();
    private final DoubleProperty prix = new SimpleDoubleProperty();
    private final IntegerProperty quantite = new SimpleIntegerProperty(0);

    public Produit() {}

    public Produit(int id, String nomp, double prix, int quantite) {
        setId(id);
        setNomp(nomp);
        setPrix(prix);
        setQuantite(quantite);
    }

    // ========== GETTERS ET SETTERS ==========
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getNomp() { return nomp.get(); }
    public void setNomp(String v) { nomp.set(v); }
    public StringProperty nompProperty() { return nomp; }

    // Alias pour compatibilité avec les vues (getNom, getPrenom, etc.)
    public String getNom() { return nomp.get(); }
    public void setNom(String v) { nomp.set(v); }

    public double getPrix() { return prix.get(); }
    public void setPrix(double v) { prix.set(v); }
    public DoubleProperty prixProperty() { return prix; }

    public int getQuantite() { return quantite.get(); }
    public void setQuantite(int v) { quantite.set(v); }
    public IntegerProperty quantiteProperty() { return quantite; }

    // Alias pour compatibilité
    public int getQuantiteStock() { return quantite.get(); }

    // ========== MÉTHODES UTILITAIRES ==========
    public boolean isInStock() {
        return quantite.get() > 0;
    }

    public boolean isLowStock() {
        return quantite.get() <= 5 && quantite.get() > 0;
    }

    public boolean isOutOfStock() {
        return quantite.get() <= 0;
    }

    @Override
    public String toString() {
        return getNomp() + " - " + String.format("%,.0f", getPrix()) + " FCFA";
    }
}