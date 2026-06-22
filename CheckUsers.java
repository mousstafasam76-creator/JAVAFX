import java.sql.*;

public class CheckUsers {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/facturation", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username, password, role FROM users");
            
            System.out.println("=== UTILISATEURS ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                    " | Username: '" + rs.getString("username") + "'" +
                    " | Password: '" + rs.getString("password") + "'" +
                    " | Role: '" + rs.getString("role") + "'");
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
