package com.inapp.view.front.commande;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Commande;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Detail extends VBox {
    
    private NavigationManager navigationManager;
    private int commandeId;
    private Commande commande;
    
    public Detail(NavigationManager navManager, int id) {
        this.navigationManager = navManager;
        this.commandeId = id;
        loadCommandeData();
        setupUI();
    }
    
    private void loadCommandeData() {
        commande = new Commande(commandeId, "Jean Dupont", LocalDate.now().toString(), "en_attente", 250000, "Réfrigérateur Samsung (2), Lave-linge LG (1)");
    }
    
    private void setupUI() {
        setPadding(new Insets(25));
        setSpacing(20);
        setStyle("-fx-background-color: #f8f9fa;");
        
        VBox content = new VBox(20);
        content.getChildren().add(createHeader());
        
        // Grille d'informations
        GridPane infoGrid = createInfoGrid();
        infoGrid.setStyle("-fx-background-color: white; -fx-background-radius: 16px;");
        infoGrid.setPadding(new Insets(25));
        infoGrid.setHgap(20);
        infoGrid.setVgap(15);
        content.getChildren().add(infoGrid);
        
        // Tableau des produits
        VBox produitsCard = createProduitsCard();
        content.getChildren().add(produitsCard);
        
        // Boutons d'action
        HBox actionsBox = createActionsBox();
        content.getChildren().add(actionsBox);
        
        getChildren().add(content);
        
        // Animation
        setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), this);
        tt.setFromY(20);
        tt.setToY(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), this);
        ft.setFromValue(0);
        ft.setToValue(1);
        tt.play();
        ft.play();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = new Button("← Retour");
        backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> navigationManager.navigateTo("commandesList"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label title = new Label("📋 Détail commande #" + commandeId);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        header.getChildren().addAll(backBtn, spacer, title);
        return header;
    }
    
    private GridPane createInfoGrid() {
        GridPane grid = new GridPane();
        
        // Client
        Label clientLabel = new Label("Client :");
        clientLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label clientValue = new Label(commande.getClientName());
        clientValue.setStyle("-fx-text-fill: #2d3748;");
        
        // Date
        Label dateLabel = new Label("Date :");
        dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label dateValue = new Label(commande.getDateCommande());
        dateValue.setStyle("-fx-text-fill: #2d3748;");
        
        // Statut
        Label statusLabel = new Label("Statut :");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label statusValue = new Label();
        String status = commande.getStatut();
        if ("en_attente".equals(status)) {
            statusValue.setText("⏳ En attente");
            statusValue.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        } else if ("livree".equals(status)) {
            statusValue.setText("✅ Livrée");
            statusValue.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        } else {
            statusValue.setText("❌ Annulée");
            statusValue.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
        
        // Facture
        Label factureLabel = new Label("Facture :");
        factureLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Hyperlink factureLink = new Hyperlink("Non générée");
        factureLink.setStyle("-fx-text-fill: #E66239;");
        
        grid.add(clientLabel, 0, 0);
        grid.add(clientValue, 1, 0);
        grid.add(dateLabel, 0, 1);
        grid.add(dateValue, 1, 1);
        grid.add(statusLabel, 0, 2);
        grid.add(statusValue, 1, 2);
        grid.add(factureLabel, 0, 3);
        grid.add(factureLink, 1, 3);
        
        return grid;
    }
    
    private VBox createProduitsCard() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px;");
        card.setPadding(new Insets(20));
        
        Label title = new Label("📦 Produits commandés");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");
        
        // Tableau des produits
        GridPane headerGrid = new GridPane();
        headerGrid.setHgap(10);
        headerGrid.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0; -fx-padding: 10 0;");
        
        Label produitHeader = new Label("Produit");
        produitHeader.setStyle("-fx-font-weight: bold;");
        Label prixHeader = new Label("Prix unitaire");
        prixHeader.setStyle("-fx-font-weight: bold;");
        Label qtyHeader = new Label("Quantité");
        qtyHeader.setStyle("-fx-font-weight: bold;");
        Label totalHeader = new Label("Total");
        totalHeader.setStyle("-fx-font-weight: bold;");
        
        headerGrid.add(produitHeader, 0, 0);
        headerGrid.add(prixHeader, 1, 0);
        headerGrid.add(qtyHeader, 2, 0);
        headerGrid.add(totalHeader, 3, 0);
        
        VBox produitsList = new VBox(5);
        
        // Produits mock
        String[][] produits = {
            {"Réfrigérateur Samsung", "250,000 FCFA", "2", "500,000 FCFA"},
            {"Lave-linge LG", "180,000 FCFA", "1", "180,000 FCFA"}
        };
        
        for (String[] p : produits) {
            GridPane row = new GridPane();
            row.setHgap(10);
            row.setPadding(new Insets(8, 0, 8, 0));
            row.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
            
            row.add(new Label(p[0]), 0, 0);
            row.add(new Label(p[1]), 1, 0);
            row.add(new Label(p[2]), 2, 0);
            Label totalLabel = new Label(p[3]);
            totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E66239;");
            row.add(totalLabel, 3, 0);
            
            produitsList.getChildren().add(row);
        }
        
        // Total général
        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(15, 0, 0, 0));
        
        VBox totalContainer = new VBox(5);
        totalContainer.setAlignment(Pos.CENTER_RIGHT);
        totalContainer.setStyle("-fx-background-color: #f8fafc; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        totalContainer.setPadding(new Insets(15, 25, 15, 25));
        
        HBox totalRow = new HBox(20);
        Label totalText = new Label("TOTAL");
        totalText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label totalValue = new Label(String.format("%,.0f FCFA", commande.getMontantTotal()));
        totalValue.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #E66239;");
        totalRow.getChildren().addAll(totalText, totalValue);
        
        totalContainer.getChildren().add(totalRow);
        totalBox.getChildren().add(totalContainer);
        
        card.getChildren().addAll(title, headerGrid, produitsList, totalBox);
        return card;
    }
    
    private HBox createActionsBox() {
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        actionsBox.setPadding(new Insets(15, 0, 0, 0));
        
        if (!"livree".equals(commande.getStatut())) {
            Button livrerBtn = new Button("✅ Marquer livrée");
            livrerBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            livrerBtn.setOnAction(e -> {
                AlertUtils.confirmDelete("Marquer cette commande comme livrée ? Une facture sera générée.");
                AlertUtils.showSuccessMessage("Commande marquée comme livrée");
                navigationManager.navigateTo("commandesList");
            });
            actionsBox.getChildren().add(livrerBtn);
        }
        
        if (!"annulee".equals(commande.getStatut())) {
            Button annulerBtn = new Button("❌ Annuler");
            annulerBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            annulerBtn.setOnAction(e -> {
                if (AlertUtils.confirmDelete("Annuler cette commande ?")) {
                    AlertUtils.showSuccessMessage("Commande annulée");
                    navigationManager.navigateTo("commandesList");
                }
            });
            actionsBox.getChildren().add(annulerBtn);
        }
        
        if (!"en_attente".equals(commande.getStatut())) {
            Button reattendreBtn = new Button("🔄 Repasser en attente");
            reattendreBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            reattendreBtn.setOnAction(e -> {
                AlertUtils.showSuccessMessage("Commande repassée en attente");
                navigationManager.navigateTo("commandesList");
            });
            actionsBox.getChildren().add(reattendreBtn);
        }
        
        if (!"livree".equals(commande.getStatut())) {
            Button modifierBtn = new Button("✏️ Modifier");
            modifierBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            modifierBtn.setOnAction(e -> navigationManager.navigateTo("commandeEdit?id=" + commandeId));
            actionsBox.getChildren().add(modifierBtn);
        }
        
        return actionsBox;
    }
}