package com.inapp.view.front.facture;

import com.inapp.config.DatabaseConfig;
import com.inapp.controller.front.FactureController;
import com.inapp.model.Facture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FactureDetail {

    private static int factureId;
    private FactureController controller;
    private Runnable backToList;
    private Runnable navigateToEdit;
    private Runnable navigateToPrint;

    public FactureDetail(FactureController controller) { this.controller = controller; }
    public static void setFactureId(int id) { factureId = id; }
    public static int getFactureId() { return factureId; }
    public void setBackToList(Runnable r) { this.backToList = r; }
    public void setNavigateToEdit(Runnable r) { this.navigateToEdit = r; }
    public void setNavigateToPrint(Runnable r) { this.navigateToPrint = r; }

    public VBox createView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");

        Facture facture = controller.getFilteredFactures().stream()
                .filter(f -> f.getId() == factureId).findFirst().orElse(null);
        if (facture == null) {
            root.getChildren().add(new Label("Facture introuvable."));
            return root;
        }

        // Données BD
        ObservableList<Map<String, Object>> commandesLiees = getCommandesLiees(factureId);
        Map<String, Object> client = commandesLiees.isEmpty() ? null : commandesLiees.get(0);
        int clientId = client != null ? (int) client.get("client_id") : 0;
        ObservableList<Map<String, Object>> disponibles = (facture.getEtatf() == 0 && clientId > 0) 
                ? getCommandesDisponibles(clientId, factureId) : FXCollections.observableArrayList();
        double totalTtc = commandesLiees.stream().mapToDouble(c -> ((Number) c.get("total_ttc")).doubleValue()).sum();

        // ========== EN-TÊTE ==========
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Retour");
        backBtn.setStyle("-fx-background-color: white; -fx-border-color: #E66239; -fx-text-fill: #E66239; -fx-background-radius: 12px; -fx-padding: 8 20; -fx-cursor: hand;");
        backBtn.setOnAction(e -> backToList.run());
        VBox titleBox = new VBox(4);
        Label title = new Label("📄 Facture " + facture.getNomf());
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label subtitle = new Label("Détail de la facture et commandes associées");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().addAll(backBtn, titleBox);

        // ========== CARTES INFO ==========
        HBox infoCards = new HBox(20);
        VBox factureCard = createInfoCard("📄 Informations facture",
                "N° Facture : " + facture.getNomf(),
                "Date d'émission : " + facture.getDatef().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                "Statut : " + (facture.isPayee() ? "✅ Payée" : "❌ Impayée"));
        
        VBox clientCard;
        if (client != null) {
            clientCard = createInfoCard("👤 Client",
                    "Nom : " + client.get("client_nom"),
                    "📞 " + (client.get("client_tel") != null ? client.get("client_tel") : "Non renseigné"),
                    "📍 " + (client.get("client_adresse") != null ? client.get("client_adresse") : "Non renseignée"));
        } else {
            clientCard = createInfoCard("👤 Client", "Aucun client associé");
        }
        infoCards.getChildren().addAll(factureCard, clientCard);

        // ========== SECTION COMMANDES ==========
        VBox commandesBox = new VBox(10);
        commandesBox.setPadding(new Insets(15));
        commandesBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 16px; -fx-background-radius: 16px;");

        HBox cmdHeader = new HBox(10);
        cmdHeader.setAlignment(Pos.CENTER_LEFT);
        Label cmdTitle = new Label("📦 Commandes regroupées");
        cmdTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label cmdCount = new Label(commandesLiees.size() + " commande(s)");
        cmdCount.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-padding: 2 10; -fx-background-radius: 10px;");
        HBox.setHgrow(cmdTitle, Priority.ALWAYS);

        Button ajouterBtn = new Button("➕ Ajouter une commande");
        ajouterBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 6 16; -fx-cursor: hand;");
        ajouterBtn.setOnAction(e -> showAddCommandeModal(factureId, disponibles, root));
        if (facture.isPayee() || disponibles.isEmpty()) {
            ajouterBtn.setDisable(true);
            ajouterBtn.setText("➕ Ajouter (aucune dispo)");
            ajouterBtn.setStyle("-fx-background-color: #a3a3a3; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 6 16;");
        }
        cmdHeader.getChildren().addAll(cmdTitle, cmdCount, ajouterBtn);

        // Tableau des commandes
        VBox tableBox = new VBox(5);
        if (commandesLiees.isEmpty()) {
            Label empty = new Label("📭 Aucune commande associée à cette facture");
            empty.setStyle("-fx-text-fill: #64748b; -fx-padding: 20;");
            tableBox.getChildren().add(empty);
        } else {
            for (Map<String, Object> cmd : commandesLiees) {
                tableBox.getChildren().add(createCommandeRow(cmd, facture, root));
            }
        }

        // Total
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_RIGHT);
        totalRow.setPadding(new Insets(10, 0, 0, 0));
        Label totalLabel = new Label("TOTAL : " + formatMoney(totalTtc));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalLabel.setStyle("-fx-text-fill: #E66239;");
        totalRow.getChildren().add(totalLabel);

        commandesBox.getChildren().addAll(cmdHeader, new Separator(), tableBox, new Separator(), totalRow);

        // ========== PIED ==========
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Button printBtn = new Button("🖨 Imprimer la facture");
        printBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 10 24; -fx-cursor: hand;");
        printBtn.setOnAction(e -> { if (navigateToPrint != null) navigateToPrint.run(); });

        Button encaisserBtn = new Button("💰 Encaisser et marquer payée");
        encaisserBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 10 24; -fx-cursor: hand;");
        encaisserBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer l'encaissement de " + formatMoney(totalTtc) + " FCFA ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) { controller.markAsPaid(facture); backToList.run(); }
            });
        });
        if (facture.isPayee() || totalTtc == 0) encaisserBtn.setDisable(true);

        footer.getChildren().addAll(printBtn, encaisserBtn);

        root.getChildren().addAll(header, infoCards, commandesBox, footer);
        return root;
    }

    // ==================== HELPERS ====================

    private VBox createInfoCard(String title, String... lines) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #E66239; -fx-border-width: 0 0 0 4; -fx-background-radius: 16px; -fx-border-radius: 16px;");
        card.setPrefWidth(350);
        Label t = new Label(title);
        t.setFont(Font.font("System", FontWeight.BOLD, 16));
        card.getChildren().add(t);
        for (String l : lines) card.getChildren().add(new Label(l));
        return card;
    }

    private HBox createCommandeRow(Map<String, Object> cmd, Facture facture, VBox root) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 5, 10, 5));
        row.setStyle("-fx-border-color: transparent transparent #e2e8f0 transparent;");
        row.getChildren().addAll(
            label("#" + cmd.get("id"), 80),
            label(cmd.get("date_commande").toString(), 120),
            label(cmd.get("produits") != null ? cmd.get("produits").toString() : "-", 250),
            label(formatMoney(((Number) cmd.get("total_ttc")).doubleValue()), 120, Pos.CENTER_RIGHT, "-fx-font-weight: bold;")
        );
        if (facture.getEtatf() == 0) {
            Button retirer = new Button("🗑");
            retirer.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand;");
            retirer.setOnAction(e -> {
                Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Retirer cette commande ?", ButtonType.YES, ButtonType.NO);
                c.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.YES) { removeCommande(factureId, (int) cmd.get("id")); refresh(root); }
                });
            });
            row.getChildren().add(retirer);
        }
        return row;
    }

    private Label label(String text, double w) { return label(text, w, Pos.CENTER_LEFT, ""); }
    private Label label(String text, double w, Pos align, String style) {
        Label l = new Label(text);
        l.setPrefWidth(w);
        l.setAlignment(align);
        if (!style.isEmpty()) l.setStyle(style);
        return l;
    }

    private void showAddCommandeModal(int factureId, ObservableList<Map<String, Object>> dispos, VBox root) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une commande à la facture");
        dialog.setHeaderText("➕ Sélectionnez une commande");

        ComboBox<String> combo = new ComboBox<>();
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> c : dispos) {
            String lbl = "#" + c.get("id") + " - " + c.get("date_commande") + " - " + formatMoney(((Number) c.get("total_ttc")).doubleValue());
            combo.getItems().add(lbl);
            map.put(lbl, (int) c.get("id"));
        }
        combo.setPromptText("-- Choisir une commande --");
        combo.setPrefWidth(400);

        Label info = new Label("Seules les commandes livrées et non encore facturées du même client apparaissent ici.");
        info.setStyle("-fx-text-fill: #0c5460; -fx-background-color: #d1ecf1; -fx-padding: 10; -fx-background-radius: 8px;");

        dialog.getDialogPane().setContent(new VBox(15, combo, info));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("Ajouter à la facture");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Annuler");

        dialog.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK && combo.getValue() != null) {
                addCommande(factureId, map.get(combo.getValue()));
                refresh(root);
            }
        });
    }

    private void refresh(VBox root) {
        root.getChildren().clear();
        root.getChildren().addAll(createView().getChildren());
    }

    // ==================== BD ====================

    private ObservableList<Map<String, Object>> getCommandesLiees(int fid) {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        String sql = """
            SELECT c.id, c.date_commande, c.total_ttc, c.statut,
                   CONCAT(cl.prenom, ' ', cl.nomc) as client_nom,
                   cl.tel as client_tel, cl.adresse as client_adresse, cl.id as client_id,
                   GROUP_CONCAT(CONCAT(p.nomp, ' (x', d.quantite, ')') SEPARATOR ', ') as produits
            FROM commandes c
            LEFT JOIN clients cl ON c.client_id = cl.id
            LEFT JOIN detail_commande d ON c.id = d.commande_id
            LEFT JOIN produits p ON d.produit_id = p.id
            WHERE c.facture_id = ? GROUP BY c.id ORDER BY c.date_commande DESC""";
        try (Connection cn = DatabaseConfig.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, fid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id")); m.put("date_commande", rs.getDate("date_commande").toLocalDate());
                m.put("total_ttc", rs.getDouble("total_ttc")); m.put("statut", rs.getString("statut"));
                m.put("client_nom", rs.getString("client_nom")); m.put("client_tel", rs.getString("client_tel"));
                m.put("client_adresse", rs.getString("client_adresse")); m.put("client_id", rs.getInt("client_id"));
                m.put("produits", rs.getString("produits")); list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private ObservableList<Map<String, Object>> getCommandesDisponibles(int cid, int fid) {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        String sql = "SELECT id, date_commande, total_ttc FROM commandes WHERE client_id = ? AND statut = 'livree' AND (facture_id IS NULL OR facture_id = 0)";
        try (Connection cn = DatabaseConfig.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id")); m.put("date_commande", rs.getDate("date_commande").toLocalDate());
                m.put("total_ttc", rs.getDouble("total_ttc")); list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private void addCommande(int fid, int cid) {
        try (Connection cn = DatabaseConfig.getConnection(); PreparedStatement ps = cn.prepareStatement("UPDATE commandes SET facture_id = ? WHERE id = ? AND statut = 'livree' AND facture_id IS NULL")) {
            ps.setInt(1, fid); ps.setInt(2, cid); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void removeCommande(int fid, int cid) {
        try (Connection cn = DatabaseConfig.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("UPDATE commandes SET facture_id = NULL WHERE id = ? AND facture_id = ?");
            ps.setInt(1, cid); ps.setInt(2, fid); ps.executeUpdate();
            PreparedStatement cnt = cn.prepareStatement("SELECT COUNT(*) FROM commandes WHERE facture_id = ?");
            cnt.setInt(1, fid);
            ResultSet rs = cnt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                cn.prepareStatement("DELETE FROM factures WHERE id = " + fid).executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private String formatMoney(double a) { return String.format("%,.0f FCFA", a); }
}