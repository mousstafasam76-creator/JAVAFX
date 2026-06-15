package com.inapp.model;

import javafx.beans.property.*;

public class Client {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final StringProperty telephone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty adresse = new SimpleStringProperty();

    public Client() {}

    public Client(int id, String nom, String prenom) {
        setId(id);
        setNom(nom);
        setPrenom(prenom);
    }

    public Client(int id, String nom, String prenom, String telephone, String email, String adresse) {
        this(id, nom, prenom);
        setTelephone(telephone);
        setEmail(email);
        setAdresse(adresse);
    }

    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getTel() { return getTelephone(); }
    public String getTelephone() { return telephone.get(); }
    public String getEmail() { return email.get(); }
    public String getAdresse() { return adresse.get(); }
    public String getNomComplet() { return getNom() + " " + getPrenom(); }

    public void setId(int value) { id.set(value); }
    public void setNom(String value) { nom.set(value); }
    public void setPrenom(String value) { prenom.set(value); }
    public void setTelephone(String value) { telephone.set(value); }
    public void setEmail(String value) { email.set(value); }
    public void setAdresse(String value) { adresse.set(value); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty telephoneProperty() { return telephone; }
    public StringProperty emailProperty() { return email; }
    public StringProperty adresseProperty() { return adresse; }

    @Override
    public String toString() {
        return getNomComplet();
    }
}