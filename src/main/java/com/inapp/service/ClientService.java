package com.inapp.service;

import com.inapp.config.DatabaseConfig;
import com.inapp.model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY prenom, nomc";
        
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
                // Ces colonnes n'existent pas dans votre table clients
                // On les initialise à 0 par défaut
                client.setNbCommandes(0);
                client.setTotalAchats(0);
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    
    public Client getClientById(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setPrenom(rs.getString("prenom"));
                client.setNom(rs.getString("nomc"));
                client.setTelephone(rs.getString("tel"));
                client.setEmail(rs.getString("email"));
                client.setAdresse(rs.getString("adresse"));
                client.setNbCommandes(0);
                client.setTotalAchats(0);
                return client;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addClient(Client client) {
        String sql = "INSERT INTO clients (prenom, nomc, tel, email, adresse) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, client.getPrenom());
            ps.setString(2, client.getNom());
            ps.setString(3, client.getTelephone());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getAdresse());
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                client.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateClient(Client client) {
        String sql = "UPDATE clients SET prenom = ?, nomc = ?, tel = ?, email = ?, adresse = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getPrenom());
            ps.setString(2, client.getNom());
            ps.setString(3, client.getTelephone());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getAdresse());
            ps.setInt(6, client.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteClient(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteClient(Client client) {
        deleteClient(client.getId());
    }
    
    // Méthode pour obtenir le nombre de commandes d'un client
    public int getNbCommandes(int clientId) {
        String sql = "SELECT COUNT(*) FROM commandes WHERE client_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Méthode pour obtenir le total des achats d'un client
    public double getTotalAchats(int clientId) {
        String sql = "SELECT COALESCE(SUM(total_ttc), 0) FROM commandes WHERE client_id = ? AND statut = 'livree'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}