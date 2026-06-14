 package com.inapp.view.front.facture;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FacturePrint {
    public static void show(int factureId) {
        Stage stage = new Stage();
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");
        Label lbl = new Label("Aperçu de la facture #" + factureId);
        Button printBtn = new Button("Imprimer");
        printBtn.getStyleClass().add("btn-primary-custom");
        printBtn.setOnAction(e -> System.out.println("Impression..."));
        root.getChildren().addAll(lbl, printBtn);
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(FacturePrint.class.getResource("/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Impression");
        stage.show();
    }
}