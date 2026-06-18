package com.inapp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    
    private static final String URL = "jdbc:mysql://localhost:3306/facturation_java?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL chargé !");
            // Test de connexion
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("✅ Connexion à facturation_java réussie !");
            } catch (SQLException e) {
                System.err.println("❌ Test de connexion échoué: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL introuvable !");
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        System.out.println("🔌 Nouvelle connexion demandée");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}