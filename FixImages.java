import java.sql.*;

public class FixImages {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/facturation", "root", "");
            Statement stmt = conn.createStatement();
            
            // Afficher les images actuelles
            System.out.println("=== AVANT ===");
            ResultSet rs = stmt.executeQuery("SELECT id, nomcat, image FROM categories");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("nomcat") + " | " + rs.getString("image"));
            }
            
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
