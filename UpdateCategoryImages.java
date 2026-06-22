import java.sql.*;

public class UpdateCategoryImages {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/facturation", "root", "");
            Statement stmt = conn.createStatement();
            
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/telephone.png' WHERE nomcat = 'telephone'");
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/ordinateur.png' WHERE nomcat = 'Ordinateur'");
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/tv.png' WHERE nomcat = 'TV'");
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/accessoires.jpg' WHERE nomcat = 'accessoires'");
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/son_audio.png' WHERE nomcat = 'son et audio'");
            stmt.executeUpdate("UPDATE categories SET image = '/images/categories/boite.png' WHERE nomcat = 'BOITE'");
            
            System.out.println("Images mises a jour !");
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
