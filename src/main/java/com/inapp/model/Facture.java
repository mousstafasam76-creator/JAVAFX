package com.inapp.model;

import java.time.LocalDateTime;

public class Facture {
    private int id;
    private String nomf;           // Numéro de facture (ex: FACT-2024-001)
    private LocalDateTime datef;   // Date d'émission
    private int etatf;             // 0 = impayée, 1 = payée
    private int nbCommandes;       // Nombre de commandes liées
    private double montantTotal;   // Somme des commandes
    private String clients;        // Noms des clients

    public Facture(int id, String nomf, LocalDateTime datef, int etatf,
                   int nbCommandes, double montantTotal, String clients) {
        this.id = id;
        this.nomf = nomf;
        this.datef = datef;
        this.etatf = etatf;
        this.nbCommandes = nbCommandes;
        this.montantTotal = montantTotal;
        this.clients = clients;
    }

    public int getId() { return id; }
    public String getNomf() { return nomf; }
    public LocalDateTime getDatef() { return datef; }
    public int getEtatf() { return etatf; }
    public int getNbCommandes() { return nbCommandes; }
    public double getMontantTotal() { return montantTotal; }
    public String getClients() { return clients; }
    public boolean isPayee() { return etatf == 1; }
}