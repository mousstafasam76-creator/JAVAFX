package com.inapp.model;

import javafx.beans.property.*;

public class Client {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final StringProperty telephone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty adresse = new SimpleStringProperty();
    private final IntegerProperty nbCommandes = new SimpleIntegerProperty(0);
    private final DoubleProperty totalAchats = new SimpleDoubleProperty(0);

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

    public Client(int id, String prenom, String nomc, String tel, String email, String adresse, int nbCommandes, double totalAchats) {
        setId(id);
        setPrenom(prenom);
        setNom(nomc);
        setTelephone(tel);
        setEmail(email);
        setAdresse(adresse);
        setNbCommandes(nbCommandes);
        setTotalAchats(totalAchats);
    }

    // ========== GETTERS ET SETTERS ==========
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String v) { nom.set(v); }
    public StringProperty nomProperty() { return nom; }

    // Alias pour getNom (utilisé par les vues Client)
    public String getNomc() { return nom.get(); }
    public void setNomc(String v) { nom.set(v); }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String v) { prenom.set(v); }
    public StringProperty prenomProperty() { return prenom; }

    public String getTelephone() { return telephone.get(); }
    public void setTelephone(String v) { telephone.set(v); }
    public StringProperty telephoneProperty() { return telephone; }

    // Alias pour getTelephone (utilisé par les vues Client)
    public String getTel() { return telephone.get(); }
    public void setTel(String v) { telephone.set(v); }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { email.set(v); }
    public StringProperty emailProperty() { return email; }

    public String getAdresse() { return adresse.get(); }
    public void setAdresse(String v) { adresse.set(v); }
    public StringProperty adresseProperty() { return adresse; }

    public int getNbCommandes() { return nbCommandes.get(); }
    public void setNbCommandes(int v) { nbCommandes.set(v); }
    public IntegerProperty nbCommandesProperty() { return nbCommandes; }

    public double getTotalAchats() { return totalAchats.get(); }
    public void setTotalAchats(double v) { totalAchats.set(v); }
    public DoubleProperty totalAchatsProperty() { return totalAchats; }

    public String getNomComplet() {
        String n = (nom.get() != null) ? nom.get() : "";
        String p = (prenom.get() != null) ? prenom.get() : "";
        return (p + " " + n).trim();
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}