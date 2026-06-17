package com.inapp.view.front.Client;

import com.inapp.controller.front.ClientController;
import com.inapp.model.Client;
import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Footer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ViewClientView extends BorderPane {

    private static final String PRIMARY = "#E66239";
    private static final String BORDER  = "#e2e8f0";

    private final NavigationManager navManager;
    private final int clientId;

    public ViewClientView(NavigationManager navManager, int clientId) {
        this.navManager = navManager;
        this.clientId = clientId;
        setupUI();
    }

    private void setupUI() {
        // SUPPRIMER le Sidebar ici (déjà dans MainApplication)
        setBottom(new Footer());

        Client client = ClientController.getInstance().findById(clientId);

        VBox root = new VBox(24);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Retour");
        backBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10;" +
            "-fx-background-radius: 10; -fx-padding: 8 18; -fx-font-size: 13px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navManager.navigateTo("clients"));

        VBox titleBox = new VBox(3);
        Label title = new Label("👤 Fiche client");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label sub = new Label(client != null ? client.getNomComplet() : "");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("✏️ Modifier");
        editBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 9 20; -fx-background-radius: 12; -fx-font-size: 13px; -fx-cursor: hand;");
        editBtn.setOnAction(e -> navManager.navigateTo("clientEdit?id=" + clientId));

        header.getChildren().addAll(backBtn, titleBox, spacer, editBtn);

        if (client == null) {
            Label err = new Label("Client introuvable.");
            err.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            root.getChildren().addAll(header, err);
            setCenter(wrapScroll(root));
            return;
        }

        HBox statsRow = new HBox(20);
        String ca = NumberFormat.getNumberInstance(Locale.FRENCH).format((long) client.getTotalAchats()) + " FCFA";
        statsRow.getChildren().addAll(
            createStatCard("Commandes", String.valueOf(client.getNbCommandes()), PRIMARY),
            createStatCard("Chiffre d'affaires", ca, "#10b981"),
            createStatCard("Statut", client.getNbCommandes() > 0 ? "🟢 Actif" : "🔴 Inactif",
                client.getNbCommandes() > 0 ? "#10b981" : "#64748b")
        );

        VBox infoCard = new VBox(16);
        infoCard.setPadding(new Insets(24));
        infoCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 20;" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        Label infoTitle = new Label("📋 Informations du client");
        infoTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 8 0;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(12);
        addInfoRow(infoGrid, 0, "Prénom", client.getPrenom());
        addInfoRow(infoGrid, 1, "Nom", client.getNomc() != null ? client.getNomc() : "-");
        addInfoRow(infoGrid, 2, "Téléphone", client.getTel() != null ? client.getTel() : "-");
        addInfoRow(infoGrid, 3, "Email", client.getEmail() != null ? client.getEmail() : "-");
        addInfoRow(infoGrid, 4, "Adresse", client.getAdresse() != null ? client.getAdresse() : "-");
        addInfoRow(infoGrid, 5, "Total commandes", String.valueOf(client.getNbCommandes()));
        addInfoRow(infoGrid, 6, "Total achats", String.format("%,.0f FCFA", client.getTotalAchats()));
        ColumnConstraints lc = new ColumnConstraints(140);
        ColumnConstraints vc = new ColumnConstraints(); vc.setHgrow(Priority.ALWAYS);
        infoGrid.getColumnConstraints().addAll(lc, vc);

        infoCard.getChildren().addAll(infoTitle, infoGrid);

        root.getChildren().addAll(header, statsRow, infoCard);
        setCenter(wrapScroll(root));
    }

    private ScrollPane wrapScroll(VBox root) {
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f5f7fa;");
        return scroll;
    }

    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label lblLbl = new Label(label);
        lblLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        card.getChildren().addAll(valLbl, lblLbl);
        return card;
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label + " :");
        lbl.setStyle("-fx-font-weight: 600; -fx-text-fill: #64748b; -fx-font-size: 13px;");
        Label val = new Label(value != null ? value : "-");
        val.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 13px;");
        val.setWrapText(true);
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }
}