package com.inapp.service;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Commande;
import com.inapp.model.Client;
import com.inapp.model.Produit;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class CommandeService {
    
    // ========== COMMANDES ==========
    
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = """
            SELECT c.id, c.client_id, c.date_commande, c.statut, c.total_ttc,
                   CONCAT(cl.prenom, ' ', cl.nomc) as client_name,
                   cl.tel as client_tel,
                   GROUP_CONCAT(CONCAT(p.nomp, ' (x', d.quantite, ')') SEPARATOR ', ') as produits
            FROM commandes c
            LEFT JOIN clients cl ON c.client_id = cl.id
            LEFT JOIN detail_commande d ON c.id = d.commande_id
            LEFT JOIN produits p ON d.produit_id = p.id
            GROUP BY c.id
            ORDER BY c.date_commande DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande cmd = new Commande();
                cmd.setId(rs.getInt("id"));
                cmd.setClientId(rs.getInt("client_id"));
                cmd.setClientName(rs.getString("client_name") != null ? rs.getString("client_name") : "Client inconnu");
                cmd.setClientTel(rs.getString("client_tel"));
                cmd.setDateCommande(rs.getDate("date_commande").toLocalDate());
                cmd.setStatut(rs.getString("statut"));
                cmd.setTotalTtc(rs.getInt("total_ttc"));
                cmd.setProduits(rs.getString("produits") != null ? rs.getString("produits") : "");
                commandes.add(cmd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    public Commande getCommandeById(int id) {
        String sql = """
            SELECT c.id, c.client_id, c.date_commande, c.statut, c.total_ttc,
                   CONCAT(cl.prenom, ' ', cl.nomc) as client_name,
                   cl.tel as client_tel
            FROM commandes c
            LEFT JOIN clients cl ON c.client_id = cl.id
            WHERE c.id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Commande cmd = new Commande();
                cmd.setId(rs.getInt("id"));
                cmd.setClientId(rs.getInt("client_id"));
                cmd.setClientName(rs.getString("client_name") != null ? rs.getString("client_name") : "Client inconnu");
                cmd.setClientTel(rs.getString("client_tel"));
                cmd.setDateCommande(rs.getDate("date_commande").toLocalDate());
                cmd.setStatut(rs.getString("statut"));
                cmd.setTotalTtc(rs.getInt("total_ttc"));
                return cmd;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Map<String, Object>> getCommandeDetails(int commandeId) {
        List<Map<String, Object>> details = new ArrayList<>();
        String sql = """
            SELECT d.id, p.id as produit_id, p.nomp as produit_nom, 
                   d.quantite, d.prix_unitaire
            FROM detail_commande d
            JOIN produits p ON d.produit_id = p.id
            WHERE d.commande_id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id", rs.getInt("id"));
                detail.put("produit_id", rs.getInt("produit_id"));
                detail.put("produit_nom", rs.getString("produit_nom"));
                detail.put("quantite", rs.getInt("quantite"));
                detail.put("prix_unitaire", rs.getInt("prix_unitaire"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    // ========== MÉTHODES POUR ADD.JAVA ==========
    
    public int addCommande(int clientId, int userId, LocalDate date, List<Map<String, Integer>> items) {
        int commandeId = -1;
        String sqlCommande = "INSERT INTO commandes (client_id, created_by, date_commande, statut) VALUES (?, ?, ?, 'en_attente')";
        String sqlDetail = "INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clientId);
                ps.setInt(2, userId);
                ps.setDate(3, java.sql.Date.valueOf(date));
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    commandeId = rs.getInt(1);
                    
                    try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                        for (Map<String, Integer> item : items) {
                            psDetail.setInt(1, commandeId);
                            psDetail.setInt(2, item.get("produit_id"));
                            psDetail.setInt(3, item.get("quantite"));
                            psDetail.setInt(4, item.get("prix_unitaire"));
                            psDetail.addBatch();
                        }
                        psDetail.executeBatch();
                    }
                    
                    updateTotalCommande(conn, commandeId);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeId;
    }
    
    // ========== MÉTHODES POUR EDIT.JAVA ==========
    
    public void updateCommande(int commandeId, int clientId, LocalDate date, 
                               List<Map<String, Integer>> items, 
                               List<Map<String, Object>> existingDetails) {
        String sqlUpdateCommande = "UPDATE commandes SET client_id = ?, date_commande = ? WHERE id = ?";
        String sqlDeleteDetails = "DELETE FROM detail_commande WHERE commande_id = ?";
        String sqlInsertDetail = "INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateCommande)) {
                ps.setInt(1, clientId);
                ps.setDate(2, java.sql.Date.valueOf(date));
                ps.setInt(3, commandeId);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteDetails)) {
                ps.setInt(1, commandeId);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertDetail)) {
                for (Map<String, Integer> item : items) {
                    ps.setInt(1, commandeId);
                    ps.setInt(2, item.get("produit_id"));
                    ps.setInt(3, item.get("quantite"));
                    ps.setInt(4, item.get("prix_unitaire"));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            updateTotalCommande(conn, commandeId);
            
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ========== MÉTHODES STANDARD (avec objets Commande) ==========
    
    public void addCommande(Commande commande, List<Map<String, Object>> details) {
        String sqlCommande = "INSERT INTO commandes (client_id, date_commande, statut, total_ttc) VALUES (?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, commande.getClientId());
                ps.setDate(2, java.sql.Date.valueOf(commande.getDateCommande()));
                ps.setString(3, commande.getStatut());
                ps.setInt(4, commande.getTotalTtc());
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int commandeId = rs.getInt(1);
                    commande.setId(commandeId);
                    
                    try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                        for (Map<String, Object> detail : details) {
                            psDetail.setInt(1, commandeId);
                            psDetail.setInt(2, (int) detail.get("produit_id"));
                            psDetail.setInt(3, (int) detail.get("quantite"));
                            psDetail.setInt(4, (int) detail.get("prix_unitaire"));
                            psDetail.addBatch();
                        }
                        psDetail.executeBatch();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateCommande(Commande commande, List<Map<String, Object>> details) {
        String sqlUpdateCommande = "UPDATE commandes SET client_id = ?, date_commande = ?, statut = ?, total_ttc = ? WHERE id = ?";
        String sqlDeleteDetails = "DELETE FROM detail_commande WHERE commande_id = ?";
        String sqlInsertDetail = "INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateCommande)) {
                ps.setInt(1, commande.getClientId());
                ps.setDate(2, java.sql.Date.valueOf(commande.getDateCommande()));
                ps.setString(3, commande.getStatut());
                ps.setInt(4, commande.getTotalTtc());
                ps.setInt(5, commande.getId());
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteDetails)) {
                ps.setInt(1, commande.getId());
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertDetail)) {
                for (Map<String, Object> detail : details) {
                    ps.setInt(1, commande.getId());
                    ps.setInt(2, (int) detail.get("produit_id"));
                    ps.setInt(3, (int) detail.get("quantite"));
                    ps.setInt(4, (int) detail.get("prix_unitaire"));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteCommande(int id) {
        String sqlDeleteDetails = "DELETE FROM detail_commande WHERE commande_id = ?";
        String sqlDeleteCommande = "DELETE FROM commandes WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteDetails)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteCommande)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateStatut(int commandeId, String statut) {
        String sql = "UPDATE commandes SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, commandeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ========== MÉTHODES PRIVÉES ==========
    
    private void updateTotalCommande(Connection conn, int commandeId) throws SQLException {
        String sql = "UPDATE commandes SET total_ttc = (SELECT COALESCE(SUM(quantite * prix_unitaire), 0) FROM detail_commande WHERE commande_id = ?) WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ps.setInt(2, commandeId);
            ps.executeUpdate();
        }
    }
    
    // ========== STATISTIQUES ==========
    
    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT statut, COUNT(*) as count FROM commandes GROUP BY statut";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int total = 0;
            while (rs.next()) {
                String statut = rs.getString("statut");
                int count = rs.getInt("count");
                stats.put(statut, count);
                total += count;
            }
            stats.put("total", total);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // ========== PRODUITS ==========
    
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT id, nomp, prix, quantite FROM produits WHERE quantite > 0 ORDER BY nomp";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produit p = new Produit();
                p.setId(rs.getInt("id"));
                p.setNomp(rs.getString("nomp"));
                p.setPrix(rs.getDouble("prix"));
                p.setQuantite(rs.getInt("quantite"));
                produits.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    
    public void updateProduitStock(int produitId, int quantite) {
        String sql = "UPDATE produits SET quantite = quantite - ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantite);
            ps.setInt(2, produitId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ========== CLIENTS ==========
    
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, prenom, nomc, tel, email, adresse FROM clients ORDER BY prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setPrenom(rs.getString("prenom"));
                client.setNom(rs.getString("nomc"));
                client.setTelephone(rs.getString("tel"));
                client.setEmail(rs.getString("email"));
                client.setAdresse(rs.getString("adresse"));
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}