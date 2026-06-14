package com.inapp.view.front.facture;

import com.inapp.controller.front.FactureController;
import com.inapp.model.Facture;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;

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

        // En-tête
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Retour");
        backBtn.getStyleClass().add("btn-outline-custom");
        backBtn.setOnAction(e -> backToList.run());
        Label title = new Label("Facture " + facture.getNomf());
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        HBox.setHgrow(title, Priority.ALWAYS);
        header.getChildren().addAll(backBtn, title);

        // Cartes info
        HBox infoCards = new HBox(20);
        VBox factureCard = new VBox(8);
        factureCard.getStyleClass().add("info-card");
        factureCard.getChildren().addAll(
            new Label("Informations facture") {{ setFont(Font.font("System", FontWeight.BOLD, 16)); }},
            new Label("N° Facture : " + facture.getNomf()),
            new Label("Date d'émission : " + facture.getDatef().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
            createStatusBadge(facture.isPayee())
        );

        VBox clientCard = new VBox(8);
        clientCard.getStyleClass().add("info-card");
        clientCard.getChildren().addAll(
            new Label("Client") {{ setFont(Font.font("System", FontWeight.BOLD, 16)); }},
            new Label("Nom : " + facture.getClients()),
            new Label("Tél : Non renseigné"),
            new Label("Adresse : Non renseignée")
        );
        infoCards.getChildren().addAll(factureCard, clientCard);

        // Commandes (placeholder)
        VBox cmdBox = new VBox(10);
        cmdBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 15;");
        HBox cmdHeader = new HBox(10);
        cmdHeader.setAlignment(Pos.CENTER_LEFT);
        Label cmdTitle = new Label("Commandes regroupées");
        cmdTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label cmdCount = new Label("0 commande(s)");
        cmdCount.getStyleClass().add("badge-unpaid"); // style neutre
        HBox.setHgrow(cmdTitle, Priority.ALWAYS);
        cmdHeader.getChildren().addAll(cmdTitle, cmdCount);
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll("Commande #101 - 150 000 FCFA", "Commande #102 - 300 000 FCFA");
        Button ajouterCmdBtn = new Button("+ Ajouter une commande");
        ajouterCmdBtn.getStyleClass().add("btn-primary-custom");
        ajouterCmdBtn.setOnAction(e -> { /* futur */ });
        if (facture.isPayee()) ajouterCmdBtn.setDisable(true);
        cmdBox.getChildren().addAll(cmdHeader, listView, ajouterCmdBtn);

        // Boutons du bas
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button printBtn = new Button("Imprimer la facture");
        printBtn.getStyleClass().add("btn-primary-custom");
        printBtn.setOnAction(e -> { if (navigateToPrint != null) navigateToPrint.run(); });
        Button encaisserBtn = new Button("Encaisser et marquer payée");
        encaisserBtn.getStyleClass().add("btn-success-custom"); // on peut ajouter cette classe
        encaisserBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 10 24;");
        encaisserBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer l'encaissement ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) { controller.markAsPaid(facture); backToList.run(); }
            });
        });
        if (facture.isPayee() || facture.getMontantTotal() == 0) encaisserBtn.setDisable(true);
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-warning-custom");
        editBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 10 24;");
        editBtn.setOnAction(e -> { if (navigateToEdit != null) navigateToEdit.run(); });
        actions.getChildren().addAll(printBtn, encaisserBtn, editBtn);

        root.getChildren().addAll(header, infoCards, cmdBox, actions);
        return root;
    }

    private HBox createStatusBadge(boolean payee) {
        Label badge = new Label(payee ? "✓ Payée" : "⚠️ Impayée");
        badge.getStyleClass().add(payee ? "badge-paid" : "badge-unpaid");
        HBox box = new HBox(badge);
        box.setPadding(new Insets(5,0,0,0));
        return box;
    }
}