package com.inapp.view.front.commande;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Commande;
import com.inapp.service.CommandeService;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Detail extends VBox {
    
    private NavigationManager navigationManager;
    private CommandeService commandeService;
    private int commandeId;
    private Commande commande;
    private List<Map<String, Object>> produitsDetails;
    
    public Detail(NavigationManager navManager, int id) {
        this.navigationManager = navManager;
        this.commandeService = new CommandeService();
        this.commandeId = id;
        System.out.println("📄 Detail créé pour la commande #" + id);
        setupUI();
        loadData();
    }
    
    private void setupUI() {
        setPadding(new Insets(25));
        setSpacing(20);
        setStyle("-fx-background-color: #f8f9fa;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox content = new VBox(20);
        content.getChildren().add(createHeader());
        content.getChildren().add(createInfoCards());
        content.getChildren().add(createProduitsCard());
        content.getChildren().add(createActionsBox());
        
        scrollPane.setContent(content);
        getChildren().add(scrollPane);
        
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
        backBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> navigationManager.navigateTo("commandesList"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label title = new Label("📋 Détail commande #" + commandeId);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        header.getChildren().addAll(backBtn, spacer, title);
        return header;
    }
    
    private HBox createInfoCards() {
        HBox cardsRow = new HBox(20);
        cardsRow.setAlignment(Pos.CENTER);
        
        VBox clientCard = new VBox(15);
        clientCard.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        clientCard.setPadding(new Insets(20));
        HBox.setHgrow(clientCard, Priority.ALWAYS);
        
        HBox clientTitle = new HBox(10);
        clientTitle.setAlignment(Pos.CENTER_LEFT);
        Label clientIcon = new Label("👤");
        clientIcon.setStyle("-fx-font-size: 18px;");
        Label clientTitleLabel = new Label("Informations client");
        clientTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");
        clientTitle.getChildren().addAll(clientIcon, clientTitleLabel);
        
        VBox clientInfo = new VBox(8);
        clientInfo.setStyle("-fx-padding: 0 0 0 28;");
        clientCard.getChildren().addAll(clientTitle, clientInfo);
        
        VBox commandeCard = new VBox(15);
        commandeCard.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        commandeCard.setPadding(new Insets(20));
        HBox.setHgrow(commandeCard, Priority.ALWAYS);
        
        HBox commandeTitle = new HBox(10);
        commandeTitle.setAlignment(Pos.CENTER_LEFT);
        Label commandeIcon = new Label("ℹ️");
        commandeIcon.setStyle("-fx-font-size: 18px;");
        Label commandeTitleLabel = new Label("Informations commande");
        commandeTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");
        commandeTitle.getChildren().addAll(commandeIcon, commandeTitleLabel);
        
        VBox commandeInfo = new VBox(8);
        commandeInfo.setStyle("-fx-padding: 0 0 0 28;");
        commandeCard.getChildren().addAll(commandeTitle, commandeInfo);
        
        cardsRow.getChildren().addAll(clientCard, commandeCard);
        cardsRow.setUserData(new Object[]{clientInfo, commandeInfo});
        
        return cardsRow;
    }
    
    private VBox createProduitsCard() {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 20, 15, 20));
        Label title = new Label("📦 Produits commandés");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");
        header.getChildren().add(title);
        
        GridPane headerGrid = new GridPane();
        headerGrid.setHgap(10);
        headerGrid.setPadding(new Insets(12, 20, 12, 20));
        headerGrid.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
        
        Label produitHeader = new Label("Produit");
        produitHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label prixHeader = new Label("Prix unitaire");
        prixHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label qtyHeader = new Label("Quantité");
        qtyHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label totalHeader = new Label("Total");
        totalHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        
        headerGrid.add(produitHeader, 0, 0);
        headerGrid.add(prixHeader, 1, 0);
        headerGrid.add(qtyHeader, 2, 0);
        headerGrid.add(totalHeader, 3, 0);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(15);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        headerGrid.getColumnConstraints().addAll(col1, col2, col3, col4);
        
        VBox produitsList = new VBox(0);
        
        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(20));
        
        VBox totalContainer = new VBox(5);
        totalContainer.setAlignment(Pos.CENTER_RIGHT);
        totalContainer.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12px;");
        totalContainer.setPadding(new Insets(15, 25, 15, 25));
        
        HBox totalRow = new HBox(20);
        totalRow.setAlignment(Pos.CENTER_RIGHT);
        Label totalText = new Label("TOTAL :");
        totalText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #4a5568;");
        Label totalValue = new Label("0 FCFA");
        totalValue.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #E66239;");
        totalRow.getChildren().addAll(totalText, totalValue);
        
        totalContainer.getChildren().add(totalRow);
        totalBox.getChildren().add(totalContainer);
        
        card.getChildren().addAll(header, headerGrid, produitsList, totalBox);
        card.setUserData(new Object[]{produitsList, totalValue});
        
        return card;
    }
    
    private HBox createActionsBox() {
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        actionsBox.setPadding(new Insets(15, 0, 0, 0));
        
        if (commande != null && !"livree".equals(commande.getStatut())) {
            Button livrerBtn = new Button("✅ Marquer livrée");
            livrerBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            livrerBtn.setOnAction(e -> {
                if (AlertUtils.confirmDialog("Confirmation", "Marquer cette commande comme livrée ?")) {
                    new Thread(() -> {
                        try {
                            commandeService.updateStatut(commandeId, "livree");
                            Platform.runLater(() -> {
                                if (commande != null) commande.setStatut("livree");
                                AlertUtils.showSuccessMessage("Commande marquée comme livrée");
                                refreshData();
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de la mise à jour: " + ex.getMessage()));
                        }
                    }).start();
                }
            });
            actionsBox.getChildren().add(livrerBtn);
        }
        
        if (commande != null && !"annulee".equals(commande.getStatut())) {
            Button annulerBtn = new Button("❌ Annuler");
            annulerBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            annulerBtn.setOnAction(e -> {
                String message = "⚠️ Annuler cette commande ?\n\n";
                if ("livree".equals(commande.getStatut())) {
                    message += "Le stock sera restauré.";
                }
                if (AlertUtils.confirmDialog("Confirmation", message)) {
                    new Thread(() -> {
                        try {
                            commandeService.updateStatut(commandeId, "annulee");
                            Platform.runLater(() -> {
                                if (commande != null) commande.setStatut("annulee");
                                AlertUtils.showSuccessMessage("Commande annulée");
                                refreshData();
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de l'annulation: " + ex.getMessage()));
                        }
                    }).start();
                }
            });
            actionsBox.getChildren().add(annulerBtn);
        }
        
        if (commande != null && !"en_attente".equals(commande.getStatut())) {
            Button reattendreBtn = new Button("🔄 Repasser en attente");
            reattendreBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            reattendreBtn.setOnAction(e -> {
                if (AlertUtils.confirmDialog("Confirmation", "Repasser cette commande en attente ?")) {
                    new Thread(() -> {
                        try {
                            commandeService.updateStatut(commandeId, "en_attente");
                            Platform.runLater(() -> {
                                if (commande != null) commande.setStatut("en_attente");
                                AlertUtils.showSuccessMessage("Commande repassée en attente");
                                refreshData();
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de la mise à jour: " + ex.getMessage()));
                        }
                    }).start();
                }
            });
            actionsBox.getChildren().add(reattendreBtn);
        }
        
        if (commande != null && !"livree".equals(commande.getStatut())) {
            Button modifierBtn = new Button("✏️ Modifier");
            modifierBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
            modifierBtn.setOnAction(e -> {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(commandeId));
                navigationManager.navigateToWithParams("commandeEdit", params);
            });
            actionsBox.getChildren().add(modifierBtn);
        }
        
        return actionsBox;
    }
    
    private void loadData() {
        System.out.println("🔄 Chargement des données pour la commande #" + commandeId);
        new Thread(() -> {
            try {
                commande = commandeService.getCommandeById(commandeId);
                produitsDetails = commandeService.getCommandeDetails(commandeId);
                
                Platform.runLater(() -> {
                    if (commande == null) {
                        AlertUtils.showErrorMessage("Commande non trouvée");
                        navigationManager.navigateTo("commandesList");
                        return;
                    }
                    System.out.println("✅ Données chargées pour la commande #" + commandeId);
                    updateClientCard();
                    updateCommandeCard();
                    updateProduitsCard();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur de chargement: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }
    
    private void updateClientCard() {
        HBox cardsRow = (HBox) getChildren().get(1);
        Object[] userData = (Object[]) cardsRow.getUserData();
        VBox clientInfo = (VBox) userData[0];
        clientInfo.getChildren().clear();
        
        String clientName = commande.getClientName() != null ? commande.getClientName() : "Client inconnu";
        Label nameLabel = new Label(clientName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");
        
        HBox telBox = new HBox(8);
        Label telIcon = new Label("📞");
        String clientTel = commande.getClientTel() != null ? commande.getClientTel() : "Non renseigné";
        Label telLabel = new Label(clientTel);
        telLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        telBox.getChildren().addAll(telIcon, telLabel);
        
        HBox idBox = new HBox(8);
        Label idIcon = new Label("🆔");
        Label idLabel = new Label("Commande #" + commandeId);
        idLabel.setStyle("-fx-text-fill: #E66239; -fx-font-size: 12px; -fx-font-weight: bold;");
        idBox.getChildren().addAll(idIcon, idLabel);
        
        clientInfo.getChildren().addAll(nameLabel, telBox, idBox);
    }
    
    private void updateCommandeCard() {
        HBox cardsRow = (HBox) getChildren().get(1);
        Object[] userData = (Object[]) cardsRow.getUserData();
        VBox commandeInfo = (VBox) userData[1];
        commandeInfo.getChildren().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        HBox dateBox = new HBox(8);
        Label dateIcon = new Label("📅");
        String dateStr = commande.getDateCommande() != null ? commande.getDateCommande().format(formatter) : "N/A";
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-text-fill: #2d3748;");
        dateBox.getChildren().addAll(dateIcon, dateLabel);
        
        HBox statusBox = new HBox(8);
        Label statusIcon = new Label("📊");
        Label statusLabel = new Label();
        if ("en_attente".equals(commande.getStatut())) {
            statusLabel.setText("⏳ En attente");
            statusLabel.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px;");
        } else if ("livree".equals(commande.getStatut())) {
            statusLabel.setText("✅ Livrée");
            statusLabel.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px;");
        } else {
            statusLabel.setText("❌ Annulée");
            statusLabel.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px;");
        }
        statusBox.getChildren().addAll(statusIcon, statusLabel);
        
        HBox totalBox = new HBox(8);
        Label totalIcon = new Label("💰");
        Label totalLabel = new Label(String.format("%,d FCFA", commande.getTotalTtc()));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E66239; -fx-font-size: 14px;");
        totalBox.getChildren().addAll(totalIcon, totalLabel);
        
        commandeInfo.getChildren().addAll(dateBox, statusBox, totalBox);
    }
    
    private void updateProduitsCard() {
        VBox produitsCard = (VBox) getChildren().get(2);
        Object[] userData = (Object[]) produitsCard.getUserData();
        VBox produitsList = (VBox) userData[0];
        Label totalValue = (Label) userData[1];
        
        produitsList.getChildren().clear();
        
        int total = 0;
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(15);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        
        if (produitsDetails != null && !produitsDetails.isEmpty()) {
            for (Map<String, Object> detail : produitsDetails) {
                GridPane row = new GridPane();
                row.setHgap(10);
                row.setPadding(new Insets(12, 20, 12, 20));
                row.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                row.getColumnConstraints().addAll(col1, col2, col3, col4);
                
                String nom = (String) detail.get("produit_nom");
                int prix = (int) detail.get("prix_unitaire");
                int quantite = (int) detail.get("quantite");
                int ligneTotal = prix * quantite;
                total += ligneTotal;
                
                Label nomLabel = new Label(nom != null ? nom : "Produit inconnu");
                nomLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
                Label prixLabel = new Label(String.format("%,d FCFA", prix));
                prixLabel.setStyle("-fx-text-fill: #64748b;");
                Label qtyLabel = new Label(String.valueOf(quantite));
                qtyLabel.setStyle("-fx-text-fill: #64748b;");
                Label totalLigneLabel = new Label(String.format("%,d FCFA", ligneTotal));
                totalLigneLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E66239;");
                
                row.add(nomLabel, 0, 0);
                row.add(prixLabel, 1, 0);
                row.add(qtyLabel, 2, 0);
                row.add(totalLigneLabel, 3, 0);
                
                produitsList.getChildren().add(row);
            }
        } else {
            Label emptyLabel = new Label("Aucun produit trouvé pour cette commande");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-padding: 20px; -fx-alignment: center;");
            emptyLabel.setAlignment(Pos.CENTER);
            produitsList.getChildren().add(emptyLabel);
        }
        
        totalValue.setText(String.format("%,d FCFA", total));
    }
    
    private void refreshData() {
        loadData();
        getChildren().remove(3);
        getChildren().add(createActionsBox());
    }
}