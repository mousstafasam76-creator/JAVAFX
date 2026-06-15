package com.inapp.model;

import javafx.beans.property.*;

public class Produit {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nomp = new SimpleStringProperty();
    private final DoubleProperty prix = new SimpleDoubleProperty();
    private final IntegerProperty quantite = new SimpleIntegerProperty();
    
    public Produit() {}
    
    public Produit(int id, String nomp, double prix, int quantite) {
        setId(id);
        setNomp(nomp);
        setPrix(prix);
        setQuantite(quantite);
    }
    
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }
    
    public String getNomp() { return nomp.get(); }
    public void setNomp(String v) { nomp.set(v); }
    public StringProperty nompProperty() { return nomp; }
    
    public double getPrix() { return prix.get(); }
    public void setPrix(double v) { prix.set(v); }
    public DoubleProperty prixProperty() { return prix; }
    
    public int getQuantite() { return quantite.get(); }
    public void setQuantite(int v) { quantite.set(v); }
    public IntegerProperty quantiteProperty() { return quantite; }
}