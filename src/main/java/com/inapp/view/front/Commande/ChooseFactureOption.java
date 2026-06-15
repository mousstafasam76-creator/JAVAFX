 package com.inapp.view.front.commande;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;

public class ChooseFactureOption extends VBox {
    
    private NavigationManager navigationManager;
    private int commandeId;
    private int clientId;
    private String clientNom;
    private double totalTTC;
    
    public ChooseFactureOption(NavigationManager navManager, int commandeId, int clientId, String clientNom, double totalTTC) {
        this.navigationManager = navManager;
        this.commandeId = commandeId;
        this.clientId = clientId;
        this.clientNom = clientNom;
        this.totalTTC = totalTTC;
        setupUI();
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #f8f9fa;");
        setAlignment(Pos.TOP_CENTER);
        
        VBox content = new VBox(25);
        content.setMaxWidth(600);
        content.setAlignment(Pos.CENTER);
        
        // Icône
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(70, 70);
        iconBox.setStyle("-fx-background-color: #10b981; -fx-background-radius: 35px;");
        Label iconLabel = new Label("🚚");
        iconLabel.setStyle("-fx-font-size: 32px;");
        iconBox.getChildren().add(iconLabel);
        
        // Titre
        Label title = new Label("Confirmation de livraison");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        Label subtitle = new Label("Commande #" + commandeId + " - " + clientNom + " - " + String.format("%,.0f", totalTTC) + " FCFA");
        subtitle.setStyle("-fx-text-fill: #718096;");
        
        // Message info
        Label infoLabel = new Label("Comment souhaitez-vous gérer la facture pour cette livraison ?");
        infoLabel.setStyle("-fx-text-fill: #4a5568;");
        
        // Options
        HBox optionsBox = new HBox(20);
        optionsBox.setAlignment(Pos.CENTER);
        
        VBox option1 = createOptionCard("📄", "Ajouter à une facture existante", "Regrouper avec d'autres commandes", "#f59e0b");
        VBox option2 = createOptionCard("🆕", "Créer une nouvelle facture", "Facture séparée pour cette commande", "#10b981");
        
        option1.setOnMouseClicked(e -> showExistingFactures());
        option2.setOnMouseClicked(e -> createNewFacture());
        
        optionsBox.getChildren().addAll(option1, option2);
        
        // Lien avancé
        Hyperlink advancedLink = new Hyperlink("Options avancées");
        advancedLink.setStyle("-fx-text-fill: #E66239;");
        
        content.getChildren().addAll(iconBox, title, subtitle, infoLabel, optionsBox, advancedLink);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        getChildren().add(scrollPane);
    }
    
    private VBox createOptionCard(String icon, String title, String description, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-radius: 16px; -fx-cursor: hand;");
        card.setPrefWidth(250);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2d3748;");
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(iconLabel, titleLabel, descLabel);
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #fef4f0; -fx-background-radius: 16px; -fx-border-color: #E66239; -fx-border-radius: 16px; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-radius: 16px; -fx-cursor: hand;"));
        
        return card;
    }
    
    private void showExistingFactures() {
        AlertUtils.showInfoMessage("Fonctionnalité: Ajout à facture existante");
        navigationManager.navigateTo("commandesList");
    }
    
    private void createNewFacture() {
        AlertUtils.showSuccessMessage("Nouvelle facture créée et commande livrée");
        navigationManager.navigateTo("commandesList");
    }
}