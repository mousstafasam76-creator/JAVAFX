package com.inapp.view.front.client;

import com.inapp.controller.front.ClientController;
import com.inapp.model.Client;
import com.inapp.utils.AlertUtils;
import com.inapp.utils.NavigationManager;
import com.inapp.view.front.FrontView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AddClientView extends FrontView {

    private static final String PRIMARY = "#E66239";
    private static final String BORDER  = "#e2e8f0";

    public AddClientView(NavigationManager navManager) {
        super(navManager);
    }

    @Override
    protected Node createContent() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f7fa;");

        // En-tête
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Retour");
        backBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10;" +
                "-fx-background-radius: 10; -fx-padding: 8 18; -fx-font-size: 13px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigationManager.navigateTo("clientsList"));

        VBox titleBox = new VBox(3);
        Label title = new Label("➕ Nouveau client");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label sub = new Label("Ajoutez un nouveau client à votre portefeuille");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(title, sub);
        header.getChildren().addAll(backBtn, titleBox);

        // Formulaire
        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(28));
        formCard.setMaxWidth(720);
        formCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20;" +
                        "-fx-border-color: " + BORDER + "; -fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        // Champs
        TextField prenomField = createField("Prénom *", "Jean");
        TextField nomField    = createField("Nom", "Dupont");
        TextField telField    = createField("Téléphone", "77-90-34-44");
        TextField emailField  = createField("Email", "client@exemple.com");
        TextArea adresseField = new TextArea();
        adresseField.setPromptText("Adresse complète...");
        adresseField.setPrefRowCount(2);
        adresseField.setStyle("-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-font-size: 13px; -fx-padding: 10 14;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.add(buildFieldBox("Prénom", prenomField, true), 0, 0);
        grid.add(buildFieldBox("Nom", nomField, false), 1, 0);
        grid.add(buildFieldBox("Téléphone", telField, false), 0, 1);
        grid.add(buildFieldBox("Email", emailField, false), 1, 1);
        // Adresse sur 2 colonnes
        VBox adresseBox = buildFieldBox("Adresse", adresseField, false);
        grid.add(adresseBox, 0, 2, 2, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Boutons
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;" +
                "-fx-background-radius: 12; -fx-padding: 10 24; -fx-font-size: 13px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> navigationManager.navigateTo("clientsList"));

        Button saveBtn = new Button("💾 Enregistrer");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-padding: 10 24; -fx-background-radius: 12; -fx-font-size: 13px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            String prenom = prenomField.getText().trim();
            String nom    = nomField.getText().trim();
            String tel    = telField.getText().trim();
            String email  = emailField.getText().trim();
            String adresse = adresseField.getText().trim();

            if (prenom.isEmpty()) {
                AlertUtils.showErrorMessage("Le prénom est obligatoire !");
                return;
            }
            if (!email.isEmpty() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
                AlertUtils.showErrorMessage("Email invalide !");
                return;
            }

            Client c = new Client();
            c.setPrenom(prenom);
            c.setNomc(nom);
            c.setTel(tel);
            c.setEmail(email);
            c.setAdresse(adresse);
            c.setNbCommandes(0);
            c.setTotalAchats(0);

            ClientController.getInstance().addClient(c);
            AlertUtils.showSuccessMessage("Client ajouté avec succès !");
            navigationManager.navigateTo("clientsList");
        });

        actions.getChildren().addAll(cancelBtn, saveBtn);
        formCard.getChildren().addAll(grid, actions);

        root.getChildren().addAll(header, formCard);
        return root;
    }

    private TextField createField(String prompt, String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle("-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-font-size: 13px; -fx-padding: 10 14;");
        return tf;
    }

    private VBox buildFieldBox(String label, Control field, boolean required) {
        VBox box = new VBox(6);
        Label lbl = new Label(label + (required ? " *" : ""));
        lbl.setStyle("-fx-font-weight: 600; -fx-font-size: 13px; -fx-text-fill: #374151;");
        ((Region) field).setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, field);
        return box;
    }
}