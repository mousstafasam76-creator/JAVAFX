package com.inapp.view.front.facture;

import com.inapp.config.DatabaseConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class FacturePrint {

    public static void show(int factureId) {
        Stage stage = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white;");
        root.setAlignment(Pos.TOP_CENTER);

        // Récupérer la facture
        String sqlFacture = "SELECT * FROM factures WHERE id = ?";
        String sqlCommandes = """
            SELECT c.id, c.date_commande, c.total_ttc,
                   CONCAT(cl.prenom, ' ', cl.nomc) as client_nom,
                   cl.tel as client_tel, cl.adresse as client_adresse,
                   GROUP_CONCAT(CONCAT(p.nomp, ' (', d.quantite, 'x ', d.prix_unitaire, ' FCFA)') SEPARATOR ', ') as produits
            FROM commandes c
            LEFT JOIN clients cl ON c.client_id = cl.id
            LEFT JOIN detail_commande d ON c.id = d.commande_id
            LEFT JOIN produits p ON d.produit_id = p.id
            WHERE c.facture_id = ?
            GROUP BY c.id
            """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Facture
            PreparedStatement stmtF = conn.prepareStatement(sqlFacture);
            stmtF.setInt(1, factureId);
            ResultSet rsF = stmtF.executeQuery();

            if (rsF.next()) {
                String nomf = rsF.getString("nomf");
                String datef = rsF.getTimestamp("datef").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                // En-tête
                Label company = new Label("POWERSTOCK");
                company.setFont(Font.font("System", FontWeight.BOLD, 22));
                company.setStyle("-fx-text-fill: #E66239;");

                Label titre = new Label("FACTURE N° " + nomf);
                titre.setFont(Font.font("System", FontWeight.BOLD, 18));

                root.getChildren().addAll(company, new Label("Bamako - Mali"), new Label("Tel: 77-90-34-44"), new Separator(), titre);

                // Commandes
                PreparedStatement stmtC = conn.prepareStatement(sqlCommandes);
                stmtC.setInt(1, factureId);
                ResultSet rsC = stmtC.executeQuery();

                double total = 0;
                int index = 1;
                while (rsC.next()) {
                    Label cmdLabel = new Label("#" + rsC.getInt("id") + " - " + rsC.getDate("date_commande") + " - " + rsC.getString("produits") + " : " + String.format("%,.0f FCFA", rsC.getDouble("total_ttc")));
                    root.getChildren().add(cmdLabel);
                    total += rsC.getDouble("total_ttc");
                    index++;
                }

                root.getChildren().add(new Separator());
                Label totalLabel = new Label("TOTAL : " + String.format("%,.0f FCFA", total));
                totalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                totalLabel.setStyle("-fx-text-fill: #E66239;");
                root.getChildren().add(totalLabel);
            }
        } catch (SQLException e) {
            root.getChildren().add(new Label("Erreur : " + e.getMessage()));
        }

        Button printBtn = new Button("🖨 Imprimer");
        printBtn.setOnAction(e -> {
            // Imprimer (à implémenter avec PrinterJob plus tard)
            System.out.println("Impression...");
        });
        root.getChildren().add(printBtn);

        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Aperçu facture");
        stage.show();
    }
}