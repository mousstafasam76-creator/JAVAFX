import java.sql.*;

public class CheckImageUrls {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/facturation", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nomcat, image FROM categories");
            
            System.out.println("=== IMAGES EN BASE ===");
            while (rs.next()) {
                String img = rs.getString("image");
                System.out.println("ID: " + rs.getInt("id") + 
                    " | Nom: '" + rs.getString("nomcat") + "'" +
                    " | Image: '" + img + "'");
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
