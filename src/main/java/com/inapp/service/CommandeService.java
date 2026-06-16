package com.inapp.service;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Commande;
import com.inapp.model.Client;
import com.inapp.model.Produit;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeService {
    
    private Connection connection;
    
    public CommandeService() {
        try {
            this.connection = DatabaseConfig.getConnection();
            System.out.println("✅ CommandeService: Connexion BDD établie");
        } catch (SQLException e) {
            System.err.println("❌ CommandeService: Erreur de connexion - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Récupérer toutes les commandes avec produits GROUP_CONCAT
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = """
            SELECT 
                c.id,
                c.client_id,
                CONCAT(cl.prenom, ' ', cl.nomc) as client_name,
                cl.tel as client_tel,
                IFNULL(c.facture_id, 0) as facture_id,
                c.produit_id,
                c.date_commande,
                c.statut,
                c.total_ttc,
                c.created_by,
                COALESCE(GROUP_CONCAT(DISTINCT CONCAT(p.nomp, ' (', d.quantite, ')') SEPARATOR ', '), 'Aucun produit') as produits
            FROM commandes c
            LEFT JOIN clients cl ON c.client_id = cl.id
            LEFT JOIN detail_commande d ON c.id = d.commande_id
            LEFT JOIN produits p ON d.produit_id = p.id
            GROUP BY c.id
            ORDER BY c.date_commande DESC
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                commandes.add(extractCommandeFromResultSet(rs));
            }
            System.out.println("📊 CommandeService: " + commandes.size() + " commandes chargées");
        } catch (SQLException e) {
            System.err.println("❌ CommandeService: Erreur getAllCommandes - " + e.getMessage());
            e.printStackTrace();
        }
        return commandes;
    }
    
    // Récupérer les statistiques
    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN statut = 'en_attente' THEN 1 ELSE 0 END) as attente,
                SUM(CASE WHEN statut = 'livree' THEN 1 ELSE 0 END) as livree,
                SUM(CASE WHEN statut = 'annulee' THEN 1 ELSE 0 END) as annulee
            FROM commandes
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("attente", rs.getInt("attente"));
                stats.put("livree", rs.getInt("livree"));
                stats.put("annulee", rs.getInt("annulee"));
                System.out.println("📊 Stats: total=" + stats.get("total") + 
                                   ", attente=" + stats.get("attente") + 
                                   ", livree=" + stats.get("livree") + 
                                   ", annulee=" + stats.get("annulee"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // Récupérer une commande par ID - VERSION SIMPLIFIÉE ET CORRIGÉE
    public Commande getCommandeById(int id) {
        System.out.println("🔍 Recherche commande ID: " + id);
        
        // Requête simplifiée sans GROUP_CONCAT pour éviter les problèmes
        String sql = """
            SELECT 
                c.id,
                c.client_id,
                CONCAT(cl.prenom, ' ', cl.nomc) as client_name,
                cl.tel as client_tel,
                cl.email as client_email,
                cl.adresse as client_adresse,
                IFNULL(c.facture_id, 0) as facture_id,
                c.produit_id,
                c.date_commande,
                c.statut,
                c.total_ttc,
                c.created_by
            FROM commandes c
            LEFT JOIN clients cl ON c.client_id = cl.id
            WHERE c.id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Récupérer les produits séparément
                String produits = getProduitsForCommande(id);
                
                Commande cmd = new Commande(
                    rs.getInt("id"),
                    rs.getInt("client_id"),
                    rs.getString("client_name") != null ? rs.getString("client_name") : "Client inconnu",
                    rs.getString("client_tel"),
                    rs.getInt("facture_id"),
                    rs.getInt("produit_id"),
                    rs.getDate("date_commande") != null ? rs.getDate("date_commande").toLocalDate() : null,
                    rs.getString("statut"),
                    rs.getInt("total_ttc"),
                    rs.getInt("created_by"),
                    produits
                );
                System.out.println("✅ Commande trouvée: ID=" + cmd.getId() + 
                                   ", Client=" + cmd.getClientName() + 
                                   ", Statut=" + cmd.getStatut() +
                                   ", Total=" + cmd.getTotalTtc());
                return cmd;
            } else {
                System.out.println("❌ Aucune commande trouvée pour l'ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans getCommandeById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Récupérer les produits d'une commande (méthode séparée)
    private String getProduitsForCommande(int commandeId) {
        String sql = """
            SELECT GROUP_CONCAT(DISTINCT CONCAT(p.nomp, ' (', d.quantite, ')') SEPARATOR ', ') as produits
            FROM detail_commande d
            LEFT JOIN produits p ON d.produit_id = p.id
            WHERE d.commande_id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String produits = rs.getString("produits");
                return produits != null && !produits.isEmpty() ? produits : "Aucun produit";
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des produits: " + e.getMessage());
        }
        return "Aucun produit";
    }
    
    // Récupérer les détails des produits d'une commande
    public List<Map<String, Object>> getCommandeDetails(int commandeId) {
        List<Map<String, Object>> details = new ArrayList<>();
        String sql = """
            SELECT d.id, d.produit_id, d.quantite, d.prix_unitaire, p.nomp as produit_nom
            FROM detail_commande d
            LEFT JOIN produits p ON d.produit_id = p.id
            WHERE d.commande_id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id", rs.getInt("id"));
                detail.put("produit_id", rs.getInt("produit_id"));
                detail.put("produit_nom", rs.getString("produit_nom"));
                detail.put("quantite", rs.getInt("quantite"));
                detail.put("prix_unitaire", rs.getInt("prix_unitaire"));
                details.add(detail);
            }
            System.out.println("📦 " + details.size() + " produits trouvés pour la commande #" + commandeId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    // Récupérer tous les clients
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, prenom, nomc, tel, email, adresse FROM clients ORDER BY nomc";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("prenom"),
                    rs.getString("nomc"),
                    rs.getString("tel"),
                    rs.getString("email"),
                    rs.getString("adresse")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    
    // Récupérer tous les produits
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT id, nomp, prix, quantite FROM produits ORDER BY nomp";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id"),
                    rs.getString("nomp"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    
    // Créer une commande
    public int addCommande(int clientId, int userId, LocalDate dateCommande, List<Map<String, Integer>> items) throws SQLException {
        connection.setAutoCommit(false);
        int commandeId = -1;
        
        try {
            int total = 0;
            for (Map<String, Integer> item : items) {
                total += getPrixByProduitId(item.get("id")) * item.get("qty");
            }
            int premierProduitId = items.get(0).get("id");
            
            String sqlCommande = "INSERT INTO commandes (client_id, created_by, date_commande, statut, total_ttc, produit_id) VALUES (?, ?, ?, 'en_attente', ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, clientId);
                pstmt.setInt(2, userId);
                pstmt.setDate(3, Date.valueOf(dateCommande));
                pstmt.setInt(4, total);
                pstmt.setInt(5, premierProduitId);
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    commandeId = rs.getInt(1);
                }
            }
            
            for (Map<String, Integer> item : items) {
                int produitId = item.get("id");
                int quantite = item.get("qty");
                int prix = getPrixByProduitId(produitId);
                
                String sqlDetail = "INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDetail)) {
                    pstmt.setInt(1, commandeId);
                    pstmt.setInt(2, produitId);
                    pstmt.setInt(3, quantite);
                    pstmt.setInt(4, prix);
                    pstmt.executeUpdate();
                }
                
                String sqlStock = "UPDATE produits SET quantite = quantite - ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlStock)) {
                    pstmt.setInt(1, quantite);
                    pstmt.setInt(2, produitId);
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
            System.out.println("✅ Commande #" + commandeId + " créée");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        
        return commandeId;
    }
    
    // Mettre à jour une commande
    public void updateCommande(int commandeId, int clientId, LocalDate dateCommande, 
                               List<Map<String, Integer>> items, List<Map<String, Object>> oldDetails) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // Restaurer les anciens stocks
            for (Map<String, Object> old : oldDetails) {
                int produitId = (int) old.get("produit_id");
                int quantite = (int) old.get("quantite");
                String sqlRestore = "UPDATE produits SET quantite = quantite + ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlRestore)) {
                    pstmt.setInt(1, quantite);
                    pstmt.setInt(2, produitId);
                    pstmt.executeUpdate();
                }
            }
            
            // Supprimer les anciens détails
            String sqlDeleteDetails = "DELETE FROM detail_commande WHERE commande_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteDetails)) {
                pstmt.setInt(1, commandeId);
                pstmt.executeUpdate();
            }
            
            // Calculer le nouveau total
            int total = 0;
            for (Map<String, Integer> item : items) {
                total += getPrixByProduitId(item.get("id")) * item.get("qty");
            }
            
            // Mettre à jour la commande
            String sqlCommande = "UPDATE commandes SET client_id = ?, date_commande = ?, total_ttc = ?, produit_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCommande)) {
                pstmt.setInt(1, clientId);
                pstmt.setDate(2, Date.valueOf(dateCommande));
                pstmt.setInt(3, total);
                pstmt.setInt(4, items.get(0).get("id"));
                pstmt.setInt(5, commandeId);
                pstmt.executeUpdate();
            }
            
            // Insérer les nouveaux détails et mettre à jour les stocks
            for (Map<String, Integer> item : items) {
                int produitId = item.get("id");
                int quantite = item.get("qty");
                int prix = getPrixByProduitId(produitId);
                
                String sqlDetail = "INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDetail)) {
                    pstmt.setInt(1, commandeId);
                    pstmt.setInt(2, produitId);
                    pstmt.setInt(3, quantite);
                    pstmt.setInt(4, prix);
                    pstmt.executeUpdate();
                }
                
                String sqlStock = "UPDATE produits SET quantite = quantite - ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlStock)) {
                    pstmt.setInt(1, quantite);
                    pstmt.setInt(2, produitId);
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
            System.out.println("✅ Commande #" + commandeId + " mise à jour");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    // Mettre à jour le statut d'une commande
    public void updateStatut(int commandeId, String newStatus) throws SQLException {
        String sql = "UPDATE commandes SET statut = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, commandeId);
            pstmt.executeUpdate();
            System.out.println("🔄 Statut de la commande #" + commandeId + " mis à jour: " + newStatus);
        }
    }
    
    // Supprimer une commande
    public void deleteCommande(int commandeId) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            List<Map<String, Object>> details = getCommandeDetails(commandeId);
            
            for (Map<String, Object> detail : details) {
                int produitId = (int) detail.get("produit_id");
                int quantite = (int) detail.get("quantite");
                String sqlStock = "UPDATE produits SET quantite = quantite + ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlStock)) {
                    pstmt.setInt(1, quantite);
                    pstmt.setInt(2, produitId);
                    pstmt.executeUpdate();
                }
            }
            
            String sqlDeleteDetails = "DELETE FROM detail_commande WHERE commande_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteDetails)) {
                pstmt.setInt(1, commandeId);
                pstmt.executeUpdate();
            }
            
            String sqlDelete = "DELETE FROM commandes WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
                pstmt.setInt(1, commandeId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            System.out.println("🗑️ Commande #" + commandeId + " supprimée");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    // Récupérer le prix d'un produit
    private int getPrixByProduitId(int produitId) throws SQLException {
        String sql = "SELECT prix FROM produits WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, produitId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("prix");
            }
        }
        return 0;
    }
    
    private Commande extractCommandeFromResultSet(ResultSet rs) throws SQLException {
        LocalDate date = rs.getDate("date_commande") != null ? rs.getDate("date_commande").toLocalDate() : null;
        return new Commande(
            rs.getInt("id"),
            rs.getInt("client_id"),
            rs.getString("client_name") != null ? rs.getString("client_name") : "Client inconnu",
            rs.getString("client_tel"),
            rs.getInt("facture_id"),
            rs.getInt("produit_id"),
            date,
            rs.getString("statut"),
            rs.getInt("total_ttc"),
            rs.getInt("created_by"),
            rs.getString("produits") != null ? rs.getString("produits") : "Aucun produit"
        );
    }
}