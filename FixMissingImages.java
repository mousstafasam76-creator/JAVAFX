import java.sql.*;

public class FixMissingImages {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/facturation", "root", "");
            Statement stmt = conn.createStatement();
            
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/gaming.png' WHERE nomcat = 'BOITE'");
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/maison.png' WHERE nomcat = 'maison'");
            
            System.out.println("Images corrigees !");
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
