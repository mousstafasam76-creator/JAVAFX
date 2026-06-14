package com.inapp.service;

import com.inapp.model.Facture;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureService {

    public List<Facture> getAllFactures() {
        List<Facture> list = new ArrayList<>();
        list.add(new Facture(1, "FACT-2024-001", LocalDateTime.of(2024,5,10,14,30), 1, 3, 450000, "Amadou Diallo"));
        list.add(new Facture(2, "FACT-2024-002", LocalDateTime.of(2024,5,15,10,15), 0, 2, 275000, "Fatou Camara"));
        list.add(new Facture(3, "FACT-2024-003", LocalDateTime.of(2024,5,20,16,45), 0, 1, 120000, "Moussa Traoré"));
        list.add(new Facture(4, "FACT-2024-004", LocalDateTime.of(2024,5,25,9,0), 1, 4, 890000, "Aminata Keita"));
        list.add(new Facture(5, "FACT-2024-005", LocalDateTime.of(2024,5,28,11,0), 0, 2, 540000, "Oumar Sylla"));
        return list;
    }

    public int getTotal() { return getAllFactures().size(); }
    public int getPayees() { return (int) getAllFactures().stream().filter(Facture::isPayee).count(); }
    public int getImpayees() { return getTotal() - getPayees(); }
    public double getTotalImpaye() {
        return getAllFactures().stream().filter(f -> !f.isPayee()).mapToDouble(Facture::getMontantTotal).sum();
    }
}