package com.inapp.model;

import javafx.beans.property.*;

public class Client {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final StringProperty nomc = new SimpleStringProperty();
    private final StringProperty tel = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty adresse = new SimpleStringProperty();
    private final IntegerProperty nbCommandes = new SimpleIntegerProperty();
    private final DoubleProperty totalAchats = new SimpleDoubleProperty();

    public Client() {}

    public Client(int id, String prenom, String nomc, String tel, String email, String adresse, int nbCommandes, double totalAchats) {
        setId(id);
        setPrenom(prenom);
        setNomc(nomc);
        setTel(tel);
        setEmail(email);
        setAdresse(adresse);
        setNbCommandes(nbCommandes);
        setTotalAchats(totalAchats);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String v) { prenom.set(v); }
    public StringProperty prenomProperty() { return prenom; }

    public String getNomc() { return nomc.get(); }
    public void setNomc(String v) { nomc.set(v); }
    public StringProperty nomcProperty() { return nomc; }

    public String getTel() { return tel.get(); }
    public void setTel(String v) { tel.set(v); }
    public StringProperty telProperty() { return tel; }

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
        String n = (nomc.get() != null) ? nomc.get() : "";
        String p = (prenom.get() != null) ? prenom.get() : "";
        return (p + " " + n).trim();
    }
}
