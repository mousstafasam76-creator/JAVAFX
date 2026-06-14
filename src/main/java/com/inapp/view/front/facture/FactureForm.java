package com.inapp.view.front.facture;

import com.inapp.controller.front.FactureController;
import com.inapp.model.Facture;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
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
            "-fx-background-radius: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"
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
        lblNum.setStyle("-fx-text-fill: #171717;");
        TextField txtNum = new TextField();
        txtNum.setEditable(false);
        txtNum.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 12 16;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 12px;"
        );
        txtNum.setPrefWidth(350);
        if (isEdit) {
            txtNum.setText(factureToEdit.getNomf());
        } else {
            txtNum.setText("FACT-" + LocalDate.now().getYear() + "-" + String.format("%03d", controller.getTotal() + 1));
        }

        // === CLIENT ===
        Label lblClient = new Label("Client");
        lblClient.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblClient.setStyle("-fx-text-fill: #171717;");
        ComboBox<String> cmbClient = new ComboBox<>();
        cmbClient.getItems().addAll("Amadou Diallo", "Fatou Camara", "Moussa Traoré", "Aminata Keita", "Oumar Sylla");
        cmbClient.setPromptText("🔍 Choisir un client...");
        cmbClient.setPrefWidth(350);
        cmbClient.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 12px;"
        );
        if (isEdit) cmbClient.setValue(factureToEdit.getClients());

        // === DATE ===
        Label lblDate = new Label("Date d'émission");
        lblDate.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblDate.setStyle("-fx-text-fill: #171717;");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(isEdit ? factureToEdit.getDatef().toLocalDate() : LocalDate.now());
        datePicker.setPrefWidth(350);
        datePicker.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 12px;"
        );

        // === STATUT (édition seulement) ===
        Label lblStatut = new Label("Statut du paiement");
        lblStatut.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblStatut.setStyle("-fx-text-fill: #171717;");
        ComboBox<String> cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll("❌ Impayée", "✅ Payée");
        cmbStatut.setPrefWidth(350);
        cmbStatut.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 12px;"
        );
        if (isEdit) {
            cmbStatut.setValue(factureToEdit.isPayee() ? "✅ Payée" : "❌ Impayée");
        } else {
            cmbStatut.setValue("❌ Impayée");
            lblStatut.setVisible(false);
            cmbStatut.setVisible(false);
            lblStatut.setManaged(false);
            cmbStatut.setManaged(false);
        }

        // Placement dans la grille
        grid.add(lblNum, 0, 0);
        grid.add(txtNum, 1, 0);
        grid.add(lblClient, 0, 1);
        grid.add(cmbClient, 1, 1);
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
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        cancelBtn.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #334155;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 14 28;" +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(
            "-fx-background-color: #cbd5e1;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 14 28;" +
            "-fx-cursor: hand;"
        ));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #334155;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 14 28;" +
            "-fx-cursor: hand;"
        ));
        cancelBtn.setOnAction(e -> { if (onCancel != null) onCancel.run(); });

        Button saveBtn = new Button(isEdit ? "💾 Enregistrer les modifications" : "✅ Créer la facture");
        saveBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        saveBtn.setStyle(
            "-fx-background-color: #E66239;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 14 28;" +
            "-fx-cursor: hand;"
        );
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(
            "-fx-background-color: #d5542e;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 14 28;" +
            "-fx-cursor: hand;"
        ));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle(
            "-fx-background-color: #E66239;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 14 28;" +
            "-fx-cursor: hand;"
        ));

        saveBtn.setOnAction(e -> {
            // Validation
            if (cmbClient.getValue() == null || cmbClient.getValue().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un client.");
                alert.showAndWait();
                return;
            }

            String nomf = txtNum.getText();
            String client = cmbClient.getValue();
            LocalDateTime datef = datePicker.getValue().atStartOfDay();
            int etatf = cmbStatut.getValue().contains("✅") ? 1 : 0;

            if (isEdit) {
                Facture updated = new Facture(factureToEdit.getId(), nomf, datef, etatf,
                        factureToEdit.getNbCommandes(), factureToEdit.getMontantTotal(), client);
                controller.updateFacture(updated);
            } else {
                controller.addFacture(new Facture(-1, nomf, datef, etatf, 0, 0.0, client));
            }

            if (onSave != null) onSave.run();
        });

        buttonBar.getChildren().addAll(cancelBtn, saveBtn);

        root.getChildren().addAll(headerBox, formCard, buttonBar);
        return root;
    }
}