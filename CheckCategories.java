import java.sql.*;

public class CheckCategories {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/facturation", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nomcat, status, image FROM categories");
            
            System.out.println("=== CATEGORIES DANS LA BASE ===");
            System.out.println("ID | Nom                | Statut    | Image");
            System.out.println("---|--------------------|-----------|------------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nomcat");
                String status = rs.getString("status");
                String image = rs.getString("image");
                
                System.out.printf("%-3d| %-19s| %-10s| %s%n", id, 
                    (nom != null ? nom : "NULL"), 
                    (status != null ? status : "NULL"), 
                    (image != null ? image : "NULL"));
            }
            
            int count = 0;
            rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE image IS NOT NULL AND image != ''");
            if (rs.next()) count = rs.getInt(1);
            System.out.println("\nCategories avec image: " + count);
            
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
