package com.inapp.view.front.commande;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Commande;
import com.inapp.model.Produit;
import com.inapp.service.CommandeService;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class Delete extends VBox {
    
    private NavigationManager navigationManager;
    private CommandeService commandeService;
    private int commandeId;
    private Font fontAwesome;
    private Commande commande;
    private List<Produit> produits;
    
    public Delete(NavigationManager navManager, int id) {
        this.navigationManager = navManager;
        this.commandeService = new CommandeService();
        this.commandeId = id;
        this.produits = new ArrayList<>();
        loadFontAwesome();
        loadCommandeData();
        deleteCommande();
    }
    
    private void loadFontAwesome() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/fonts/fa-solid-900.ttf");
            if (fontStream != null) {
                fontAwesome = Font.loadFont(fontStream, 16);
                System.out.println("FontAwesome chargé avec succès");
            } else {
                System.err.println("FontAwesome non trouvé, utilisation des polices système");
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement FontAwesome: " + e.getMessage());
        }
    }
    
    private Text createIcon(String unicode, String color, double size) {
        Text icon = new Text(unicode);
        if (fontAwesome != null) {
            icon.setFont(Font.font(fontAwesome.getFamily(), size));
        } else {
            icon.setStyle("-fx-font-size: " + size + "px;");
        }
        if (color != null) {
            icon.setStyle(icon.getStyle() + "-fx-fill: " + color + ";");
        }
        return icon;
    }
    
    private void loadCommandeData() {
        // Récupérer les données depuis la BDD
        commande = commandeService.getCommandeById(commandeId);
        
        if (commande != null) {
            // Récupérer les produits de la commande
            List<Map<String, Object>> details = commandeService.getCommandeDetails(commandeId);
            for (Map<String, Object> detail : details) {
                Produit p = new Produit();
                p.setId((int) detail.get("produit_id"));
                p.setNomp((String) detail.get("produit_nom"));
                p.setQuantite((int) detail.get("quantite"));
                produits.add(p);
            }
        } else {
            // Données mock en cas d'erreur de BDD
            commande = new Commande();
            commande.setId(commandeId);
            commande.setClientName("Client inconnu");
            commande.setStatut("en_attente");
            produits.add(new Produit(1, "Produit exemple", 10000, 1));
        }
    }
    
    private void deleteCommande() {
        showConfirmationModal();
    }
    
    private void showConfirmationModal() {
        Stage modalStage = new Stage();
        modalStage.initStyle(StageStyle.TRANSPARENT);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        
        VBox modalContent = new VBox(20);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setPadding(new Insets(30));
        modalContent.setStyle("-fx-background-color: white; -fx-background-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 0);");
        modalContent.setMaxWidth(450);
        
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(70, 70);
        iconBox.setMinSize(70, 70);
        iconBox.setMaxSize(70, 70);
        iconBox.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 35px;");
        Text warningIcon = createIcon("\uF071", "#dc2626", 32);
        iconBox.getChildren().add(warningIcon);
        
        Label title = new Label("Suppression de la commande");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2d3748;");
        
        Label message = new Label("Êtes-vous sûr de vouloir supprimer la commande #" + commandeId + " ?");
        message.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 14px");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        
        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(15));
        detailsBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 12px;");
        
        Label detailsTitle = new Label("Cette action va :");
        detailsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2d3748;");
        
        VBox detailsList = new VBox(8);
        
        HBox detail1 = new HBox(10);
        detail1.setAlignment(Pos.CENTER_LEFT);
        Text bullet1 = createIcon("\uF054", "#E66239", 10);
        Label detail1Text = new Label("Supprimer définitivement la commande #" + commandeId);
        detail1Text.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 12px;");
        detail1.getChildren().addAll(bullet1, detail1Text);
        
        HBox detail2 = new HBox(10);
        detail2.setAlignment(Pos.CENTER_LEFT);
        Text bullet2 = createIcon("\uF054", "#10b981", 10);
        Label detail2Text = new Label("Restaurer le stock des produits concernés");
        detail2Text.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 12px;");
        detail2.getChildren().addAll(bullet2, detail2Text);
        
        HBox detail3 = new HBox(10);
        detail3.setAlignment(Pos.CENTER_LEFT);
        Text bullet3 = createIcon("\uF054", "#ef4444", 10);
        Label detail3Text = new Label("Supprimer les lignes de détail de la commande");
        detail3Text.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 12px;");
        detail3.getChildren().addAll(bullet3, detail3Text);
        
        detailsList.getChildren().addAll(detail1, detail2, detail3);
        detailsBox.getChildren().addAll(detailsTitle, detailsList);
        
        if (!produits.isEmpty()) {
            VBox produitsBox = new VBox(8);
            produitsBox.setPadding(new Insets(10, 0, 0, 0));
            
            Label produitsTitle = new Label("Produits impactés (restauration de stock) :");
            produitsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2d3748;");
            produitsBox.getChildren().add(produitsTitle);
            
            for (Produit p : produits) {
                HBox produitRow = new HBox(15);
                produitRow.setAlignment(Pos.CENTER_LEFT);
                produitRow.setPadding(new Insets(5, 0, 5, 0));
                produitRow.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
                
                Text productIcon = createIcon("\uF07A", "#E66239", 12);
                Label productName = new Label(p.getNomp());
                productName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2d3748;");
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Label qtyLabel = new Label("quantité: +" + p.getQuantite());
                qtyLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");
                
                produitRow.getChildren().addAll(productIcon, productName, spacer, qtyLabel);
                produitsBox.getChildren().add(produitRow);
            }
            detailsBox.getChildren().add(produitsBox);
        }
        
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button cancelBtn = new Button("Annuler");
        Text cancelIcon = createIcon("\uF00D", "#4a5568", 12);
        cancelBtn.setGraphic(cancelIcon);
        cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-font-size: 13px;");
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-font-size: 13px;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-font-size: 13px;"));
        cancelBtn.setOnAction(e -> {
            modalStage.close();
            navigationManager.navigateTo("commandesList");
        });
        
        Button confirmBtn = new Button("Oui, supprimer");
        Text trashIcon = createIcon("\uF1F8", "white", 12);
        confirmBtn.setGraphic(trashIcon);
        confirmBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;");
        confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        confirmBtn.setOnAction(e -> {
            modalStage.close();
            processDelete();
        });
        
        buttonsBox.getChildren().addAll(cancelBtn, confirmBtn);
        
        modalContent.getChildren().addAll(iconBox, title, message, detailsBox, buttonsBox);
        
        Scene modalScene = new Scene(modalContent);
        modalScene.setFill(Color.TRANSPARENT);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }
    
    private void processDelete() {
        showLoadingModal();
    }
    
    private void showLoadingModal() {
        Stage loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.TRANSPARENT);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        
        VBox loadingContent = new VBox(20);
        loadingContent.setAlignment(Pos.CENTER);
        loadingContent.setPadding(new Insets(40));
        loadingContent.setStyle("-fx-background-color: white; -fx-background-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 0);");
        
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #E66239;");
        
        Label loadingLabel = new Label("Suppression en cours...");
        loadingLabel.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 14px;");
        
        loadingContent.getChildren().addAll(progressIndicator, loadingLabel);
        
        Scene loadingScene = new Scene(loadingContent);
        loadingScene.setFill(Color.TRANSPARENT);
        loadingStage.setScene(loadingScene);
        loadingStage.show();
        
        new Thread(() -> {
            try {
                commandeService.deleteCommande(commandeId);
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    loadingStage.close();
                    showSuccessModal();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    AlertUtils.showErrorMessage("Erreur lors de la suppression: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showSuccessModal() {
        Stage successStage = new Stage();
        successStage.initStyle(StageStyle.TRANSPARENT);
        successStage.initModality(Modality.APPLICATION_MODAL);
        
        VBox successContent = new VBox(20);
        successContent.setAlignment(Pos.CENTER);
        successContent.setPadding(new Insets(30));
        successContent.setStyle("-fx-background-color: white; -fx-background-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 0);");
        successContent.setMaxWidth(400);
        
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(70, 70);
        iconBox.setMinSize(70, 70);
        iconBox.setMaxSize(70, 70);
        iconBox.setStyle("-fx-background-color: #d1fae5; -fx-background-radius: 35px;");
        Text successIcon = createIcon("\uF00C", "#10b981", 32);
        iconBox.getChildren().add(successIcon);
        
        Label title = new Label("Commande supprimée !");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2d3748;");
        
        Label message = new Label("La commande #" + commandeId + " a été supprimée avec succès.");
        message.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px;");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        
        VBox detailsBox = new VBox(8);
        detailsBox.setPadding(new Insets(15));
        detailsBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12px;");
        
        Label stockLabel = new Label("✓ Stock restauré pour " + produits.size() + " produit(s)");
        stockLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        
        detailsBox.getChildren().add(stockLabel);
        
        Button closeBtn = new Button("OK");
        closeBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 10px 30px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #d5542e; -fx-text-fill: white; -fx-padding: 10px 30px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 10px 30px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        closeBtn.setOnAction(e -> {
            successStage.close();
            navigationManager.navigateTo("commandesList");
        });
        
        successContent.getChildren().addAll(iconBox, title, message, detailsBox, closeBtn);
        
        Scene successScene = new Scene(successContent);
        successScene.setFill(Color.TRANSPARENT);
        successStage.setScene(successScene);
        successStage.showAndWait();
    }
}