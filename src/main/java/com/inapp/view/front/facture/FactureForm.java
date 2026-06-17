package com.inapp.view.front.facture;

import com.inapp.controller.front.FactureController;
import com.inapp.model.Facture;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FactureForm {

    private FactureController controller;
    private Facture factureToEdit;
    private Runnable onSave;
    private Runnable onCancel;

    public FactureForm(FactureController controller) { this.controller = controller; }
    public void setFactureToEdit(Facture f) { this.factureToEdit = f; }
    public void setOnSave(Runnable r) { this.onSave = r; }
    public void setOnCancel(Runnable r) { this.onCancel = r; }

    public VBox createView() {
        boolean isEdit = (factureToEdit != null);

        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8fafc;");

        // ========== EN-TÊTE ==========
        VBox headerBox = new VBox(6);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMaxWidth(600);

        Label titleLabel = new Label(isEdit ? "✏️ Modifier la facture" : "📄 Nouvelle facture");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #171717;");

        Label subtitleLabel = new Label(isEdit ?
            "Modifiez les informations de la facture existante." :
            "Remplissez les informations pour créer une nouvelle facture.");
        subtitleLabel.setFont(Font.font(14));
        subtitleLabel.setStyle("-fx-text-fill: #64748b;");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // ========== CARTE FORMULAIRE ==========
        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(30));
        formCard.setMaxWidth(600);
        formCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;"
        );
        formCard.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(500);

        // === NUMÉRO FACTURE ===
        Label lblNum = new Label("N° Facture");
        lblNum.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField txtNum = new TextField();
        txtNum.setEditable(false);
        txtNum.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 12px; -fx-padding: 12 16;");
        txtNum.setPrefWidth(350);
        if (isEdit) {
            txtNum.setText(factureToEdit.getNomf());
        } else {
            txtNum.setText("FACT-" + LocalDate.now().getYear() + "-" + String.format("%03d", controller.getTotal() + 1));
        }

        // === CLIENT (à remplacer par une vraie liste depuis la BD plus tard) ===
        Label lblClient = new Label("Client");
        lblClient.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField txtClient = new TextField();
        txtClient.setPromptText("Nom du client...");
        txtClient.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 12 16; -fx-border-color: #e2e8f0; -fx-border-radius: 12px;");
        txtClient.setPrefWidth(350);
        if (isEdit) txtClient.setText(factureToEdit.getClients());

        // === DATE ===
        Label lblDate = new Label("Date d'émission");
        lblDate.setFont(Font.font("System", FontWeight.BOLD, 14));
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(isEdit ? factureToEdit.getDatef().toLocalDate() : LocalDate.now());
        datePicker.setPrefWidth(350);
        datePicker.setStyle("-fx-background-radius: 12px; -fx-padding: 8;");

        // === STATUT (édition seulement) ===
        Label lblStatut = new Label("Statut du paiement");
        lblStatut.setFont(Font.font("System", FontWeight.BOLD, 14));
        ComboBox<String> cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll("❌ Impayée", "✅ Payée");
        cmbStatut.setPrefWidth(350);
        cmbStatut.setStyle("-fx-background-radius: 12px; -fx-padding: 8;");
        if (isEdit) {
            cmbStatut.setValue(factureToEdit.isPayee() ? "✅ Payée" : "❌ Impayée");
        } else {
            cmbStatut.setValue("❌ Impayée");
            lblStatut.setVisible(false);
            cmbStatut.setVisible(false);
            lblStatut.setManaged(false);
            cmbStatut.setManaged(false);
        }

        grid.add(lblNum, 0, 0);
        grid.add(txtNum, 1, 0);
        grid.add(lblClient, 0, 1);
        grid.add(txtClient, 1, 1);
        grid.add(lblDate, 0, 2);
        grid.add(datePicker, 1, 2);
        if (isEdit) {
            grid.add(lblStatut, 0, 3);
            grid.add(cmbStatut, 1, 3);
        }

        formCard.getChildren().add(grid);

        // ========== BOUTONS ==========
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setMaxWidth(600);

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-background-radius: 12px; -fx-padding: 14 28; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> { if (onCancel != null) onCancel.run(); });

        Button saveBtn = new Button(isEdit ? "💾 Enregistrer" : "✅ Créer la facture");
        saveBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 14 28; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            if (txtClient.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez entrer un client.").showAndWait();
                return;
            }
            String nomf = txtNum.getText();
            String client = txtClient.getText();
            LocalDateTime datef = datePicker.getValue().atStartOfDay();
            int etatf = cmbStatut.getValue() != null && cmbStatut.getValue().contains("✅") ? 1 : 0;

            if (isEdit) {
                Facture updated = new Facture(factureToEdit.getId(), nomf, datef, etatf,
                        factureToEdit.getNbCommandes(), factureToEdit.getMontantTotal(), client);
                controller.updateFacture(updated);
            } else {
                Facture newFacture = new Facture(-1, nomf, datef, etatf, 0, 0.0, client);
                controller.addFacture(newFacture);
            }
            if (onSave != null) onSave.run();
        });

        buttonBar.getChildren().addAll(cancelBtn, saveBtn);

        root.getChildren().addAll(headerBox, formCard, buttonBar);
        return root;
    }
}