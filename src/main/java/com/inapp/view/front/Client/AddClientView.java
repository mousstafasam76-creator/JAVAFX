package com.inapp.view.front.Client;

import com.inapp.controller.front.ClientController;
import com.inapp.model.Client;
import com.inapp.utils.AlertUtils;
import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Sidebar;
import com.inapp.view.components.Footer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AddClientView extends BorderPane {

    private static final String PRIMARY = "#E66239";
    private static final String BORDER  = "#e2e8f0";

    private final NavigationManager navManager;

    public AddClientView(NavigationManager navManager) {
        this.navManager = navManager;
        setupUI();
    }

    private void setupUI() {
        setLeft(new Sidebar(navManager));
        setBottom(new Footer());

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
        Label title = new Label("➕ Nouveau client");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label sub = new Label("Ajoutez un nouveau client à votre portefeuille");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(title, sub);
        header.getChildren().addAll(backBtn, titleBox);

        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(28));
        formCard.setMaxWidth(720);
        formCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 20;" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        TextField prenomField = createField();
        TextField nomField    = createField();
        TextField telField    = createField();
        TextField emailField  = createField();
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
        VBox adresseBox = buildFieldBox("Adresse", adresseField, false);
        GridPane.setColumnSpan(adresseBox, 2);
        grid.add(adresseBox, 0, 2);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50); col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;" +
            "-fx-background-radius: 12; -fx-padding: 10 24; -fx-font-size: 13px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> navManager.navigateTo("clients"));

        Button saveBtn = new Button("💾 Enregistrer");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-padding: 10 24; -fx-background-radius: 12; -fx-font-size: 13px; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            String prenom = prenomField.getText().trim();
            String email  = emailField.getText().trim();
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
            c.setNomc(nomField.getText().trim());
            c.setTel(telField.getText().trim());
            c.setEmail(email);
            c.setAdresse(adresseField.getText().trim());
            c.setNbCommandes(0);
            c.setTotalAchats(0);
            ClientController.getInstance().addClient(c);
            AlertUtils.showSuccessMessage("Client ajouté avec succès !");
            navManager.navigateTo("clients");
        });
        actions.getChildren().addAll(cancelBtn, saveBtn);

        formCard.getChildren().addAll(grid, actions);
        root.getChildren().addAll(header, formCard);

        setCenter(root);
    }

    private TextField createField() {
        TextField tf = new TextField();
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
