package com.inapp.service;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Facture;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureService {

    // Récupérer toutes les factures avec stats
    public List<Facture> getAllFactures() {
        List<Facture> list = new ArrayList<>();
        String sql = """
            SELECT 
                f.id,
                f.nomf,
                f.datef,
                f.etatf,
                COUNT(DISTINCT c.id) as nb_commandes,
                COALESCE(SUM(c.total_ttc), 0) as montant_total,
                GROUP_CONCAT(DISTINCT CONCAT(COALESCE(cl.prenom, ''), ' ', COALESCE(cl.nomc, '')) SEPARATOR ', ') as clients
            FROM factures f
            LEFT JOIN commandes c ON f.id = c.facture_id
            LEFT JOIN clients cl ON c.client_id = cl.id
            GROUP BY f.id, f.nomf, f.datef, f.etatf
            ORDER BY f.datef DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String clients = rs.getString("clients");
                list.add(new Facture(
                    rs.getInt("id"),
                    rs.getString("nomf"),
                    rs.getTimestamp("datef").toLocalDateTime(),
                    rs.getInt("etatf"),
                    rs.getInt("nb_commandes"),
                    rs.getDouble("montant_total"),
                    clients != null ? clients : "Aucun client"
                ));
            }
            System.out.println("✅ Factures chargées : " + list.size());
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Ajouter une facture
    public Facture addFacture(Facture f) {
        String sql = "INSERT INTO factures (nomf, datef, etatf) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, f.getNomf());
            // Convertir LocalDateTime en Timestamp pour la base
            stmt.setTimestamp(2, Timestamp.valueOf(f.getDatef()));
            stmt.setInt(3, f.getEtatf());
            stmt.executeUpdate();
            
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                System.out.println("✅ Facture créée : ID=" + newId);
                return new Facture(newId, f.getNomf(), f.getDatef(), f.getEtatf(), 0, 0.0, f.getClients());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout facture : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Mettre à jour une facture
    public void updateFacture(Facture f) {
        String sql = "UPDATE factures SET nomf = ?, datef = ?, etatf = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, f.getNomf());
            stmt.setTimestamp(2, Timestamp.valueOf(f.getDatef()));
            stmt.setInt(3, f.getEtatf());
            stmt.setInt(4, f.getId());
            int rows = stmt.executeUpdate();
            System.out.println("✅ Facture modifiée : " + rows + " ligne(s)");
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification facture : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Supprimer une facture (dissocie d'abord les commandes)
    public void deleteFacture(int id) {
        String unlinkSql = "UPDATE commandes SET facture_id = NULL WHERE facture_id = ?";
        String deleteSql = "DELETE FROM factures WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Transaction
            try (PreparedStatement unlinkStmt = conn.prepareStatement(unlinkSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                
                // 1. Dissocier les commandes
                unlinkStmt.setInt(1, id);
                unlinkStmt.executeUpdate();
                
                // 2. Supprimer la facture
                deleteStmt.setInt(1, id);
                int rows = deleteStmt.executeUpdate();
                
                conn.commit();
                System.out.println("✅ Facture supprimée : " + rows + " ligne(s)");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression facture : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Marquer comme payée
    public void markAsPaid(int id) {
        String sql = "UPDATE factures SET etatf = 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println("✅ Facture marquée payée : " + rows + " ligne(s)");
        } catch (SQLException e) {
            System.err.println("❌ Erreur paiement facture : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Marquer plusieurs factures comme payées
    public void markMultipleAsPaid(List<Integer> ids) {
        String sql = "UPDATE factures SET etatf = 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int id : ids) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                conn.commit();
                System.out.println("✅ " + ids.size() + " facture(s) marquée(s) payée(s)");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur paiement multiple : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Supprimer plusieurs factures
    public void deleteMultiple(List<Integer> ids) {
        String unlinkSql = "UPDATE commandes SET facture_id = NULL WHERE facture_id = ?";
        String deleteSql = "DELETE FROM factures WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement unlinkStmt = conn.prepareStatement(unlinkSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                
                for (int id : ids) {
                    unlinkStmt.setInt(1, id);
                    unlinkStmt.executeUpdate();
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
                conn.commit();
                System.out.println("✅ " + ids.size() + " facture(s) supprimée(s)");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression multiple : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Statistiques (inchangées)
    public int getTotal() { return getAllFactures().size(); }
    public int getPayees() { return (int) getAllFactures().stream().filter(Facture::isPayee).count(); }
    public int getImpayees() { return getTotal() - getPayees(); }
    public double getTotalImpaye() {
        return getAllFactures().stream().filter(f -> !f.isPayee()).mapToDouble(Facture::getMontantTotal).sum();
    }
}