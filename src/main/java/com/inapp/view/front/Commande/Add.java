package com.inapp.view.front.commande;

import javafx.animation.*;
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
import com.inapp.model.Client;
import com.inapp.model.Produit;
import com.inapp.service.CommandeService;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Add extends VBox {
    
    private NavigationManager navigationManager;
    private CommandeService commandeService;
    private int lastCommandeId;
    private Font fontAwesome;
    
    // Formulaire
    private ComboBox<Client> clientSelect;
    private DatePicker datePicker;
    private VBox produitsContainer;
    private List<ProduitLine> produitLines;
    private Label sousTotalLabel;
    private Label totalLabel;
    private Button submitBtn;
    
    // Données
    private List<Client> clients;
    private List<Produit> produits;
    
    public Add(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.commandeService = new CommandeService();
        this.produitLines = new ArrayList<>();
        loadFontAwesome();
        loadData();
        setupUI();
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
    
    private void loadData() {
        new Thread(() -> {
            clients = commandeService.getAllClients();
            produits = commandeService.getAllProduits();
            Platform.runLater(() -> {
                clientSelect.getItems().addAll(clients);
                // Ajouter une ligne par défaut
                addProduitLine();
            });
        }).start();
    }
    
    private void setupUI() {
        setPadding(new Insets(25));
        setSpacing(20);
        setStyle("-fx-background-color: #f8f9fa;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        VBox content = new VBox(20);
        content.getChildren().add(createHeader());
        
        VBox formCard = new VBox(20);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 16px;");
        formCard.setPadding(new Insets(25));
        
        formCard.getChildren().add(createClientSection());
        formCard.getChildren().add(createDateSection());
        formCard.getChildren().add(createProduitsSection());
        formCard.getChildren().add(createTotalSection());
        formCard.getChildren().add(createButtonsSection());
        
        content.getChildren().add(formCard);
        
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
        
        VBox titleBox = new VBox(5);
        HBox titleIcon = new HBox(10);
        titleIcon.setAlignment(Pos.CENTER_LEFT);
        
        Text cartIcon = createIcon("\uF07A", "#E66239", 24);
        Label title = new Label("Nouvelle commande");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        titleIcon.getChildren().addAll(cartIcon, title);
        
        Label subtitle = new Label("Saisie des ventes PowerStock");
        subtitle.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titleIcon, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backBtn = new Button("← Retour");
        backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #4a5568; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500;"));
        backBtn.setOnAction(e -> navigationManager.navigateTo("commandesList"));
        
        header.getChildren().addAll(titleBox, spacer, backBtn);
        return header;
    }
    
    private VBox createClientSection() {
        VBox section = new VBox(8);
        
        HBox labelBox = new HBox(8);
        Text userIcon = createIcon("\uF007", "#E66239", 14);
        Label label = new Label("Client *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        labelBox.getChildren().addAll(userIcon, label);
        
        clientSelect = new ComboBox<>();
        clientSelect.setPromptText("Sélectionner un client...");
        clientSelect.setStyle("-fx-padding: 10px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        clientSelect.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getPrenom() + " " + item.getNom() + " - " + item.getTel());
            }
        });
        clientSelect.setButtonCell(new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Sélectionner un client..." : item.getPrenom() + " " + item.getNom());
            }
        });
        
        HBox clientInfoBox = new HBox(10);
        clientInfoBox.setVisible(false);
        clientInfoBox.setAlignment(Pos.CENTER_LEFT);
        clientInfoBox.setPadding(new Insets(10, 0, 0, 0));
        
        Label telBadge = new Label();
        telBadge.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 11px;");
        Label adresseBadge = new Label();
        adresseBadge.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 11px;");
        clientInfoBox.getChildren().addAll(telBadge, adresseBadge);
        
        clientSelect.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                telBadge.setText("📞 " + val.getTel());
                adresseBadge.setText("📍 " + val.getAdresse());
                clientInfoBox.setVisible(true);
            } else {
                clientInfoBox.setVisible(false);
            }
        });
        
        section.getChildren().addAll(labelBox, clientSelect, clientInfoBox);
        return section;
    }
    
    private VBox createDateSection() {
        VBox section = new VBox(8);
        
        HBox labelBox = new HBox(8);
        Text calIcon = createIcon("\uF073", "#E66239", 14);
        Label label = new Label("Date de commande *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        labelBox.getChildren().addAll(calIcon, label);
        
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-padding: 10px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        
        section.getChildren().addAll(labelBox, datePicker);
        return section;
    }
    
    private VBox createProduitsSection() {
        VBox section = new VBox(10);
        
        HBox labelBox = new HBox(8);
        Text boxIcon = createIcon("\uF07A", "#E66239", 14);
        Label label = new Label("Produits commandés *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        labelBox.getChildren().addAll(boxIcon, label);
        
        GridPane headerGrid = new GridPane();
        headerGrid.setHgap(10);
        headerGrid.setPadding(new Insets(10, 0, 10, 0));
        headerGrid.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
        
        Label produitHeader = new Label("Produit");
        produitHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label prixHeader = new Label("Prix unitaire");
        prixHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label qtyHeader = new Label("Quantité");
        qtyHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label totalHeader = new Label("Total");
        totalHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label actionHeader = new Label("");
        
        headerGrid.add(produitHeader, 0, 0);
        headerGrid.add(prixHeader, 1, 0);
        headerGrid.add(qtyHeader, 2, 0);
        headerGrid.add(totalHeader, 3, 0);
        headerGrid.add(actionHeader, 4, 0);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(15);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(20);
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(5);
        headerGrid.getColumnConstraints().addAll(col1, col2, col3, col4, col5);
        
        produitsContainer = new VBox(10);
        produitsContainer.setPadding(new Insets(10, 0, 10, 0));
        
        Button addProductBtn = new Button("+ Ajouter un produit");
        addProductBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;");
        addProductBtn.setOnMouseEntered(e -> addProductBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        addProductBtn.setOnMouseExited(e -> addProductBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        addProductBtn.setOnAction(e -> addProduitLine());
        
        section.getChildren().addAll(labelBox, headerGrid, produitsContainer, addProductBtn);
        return section;
    }
    
    private void addProduitLine() {
        if (produits == null) return;
        ProduitLine line = new ProduitLine(produits, this::calculateTotal, this::showToast);
        produitLines.add(line);
        produitsContainer.getChildren().add(line);
    }
    
    private VBox createTotalSection() {
        VBox section = new VBox();
        section.setAlignment(Pos.CENTER_RIGHT);
        section.setPadding(new Insets(15, 0, 0, 0));
        
        VBox totalBox = new VBox(10);
        totalBox.setStyle("-fx-background-color: #f8fafc; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        totalBox.setPadding(new Insets(20));
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setMaxWidth(350);
        
        HBox sousTotalRow = new HBox(20);
        sousTotalRow.setAlignment(Pos.CENTER_RIGHT);
        Label sousTotalText = new Label("Sous-total :");
        sousTotalText.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        sousTotalLabel = new Label("0 FCFA");
        sousTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2d3748;");
        sousTotalRow.getChildren().addAll(sousTotalText, sousTotalLabel);
        
        Separator sep = new Separator();
        sep.setPrefWidth(250);
        
        HBox totalRow = new HBox(20);
        totalRow.setAlignment(Pos.CENTER_RIGHT);
        Label totalText = new Label("TOTAL");
        totalText.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2d3748;");
        totalLabel = new Label("0 FCFA");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: #E66239;");
        totalRow.getChildren().addAll(totalText, totalLabel);
        
        totalBox.getChildren().addAll(sousTotalRow, sep, totalRow);
        section.getChildren().add(totalBox);
        
        return section;
    }
    
    private HBox createButtonsSection() {
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setPadding(new Insets(15, 0, 0, 0));
        
        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-font-size: 13px;");
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-font-size: 13px;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-font-size: 13px;"));
        cancelBtn.setOnAction(e -> navigationManager.navigateTo("commandesList"));
        
        submitBtn = new Button("✓ Valider la commande");
        submitBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;");
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle("-fx-background-color: #d5542e; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;"));
        submitBtn.setOnAction(e -> saveCommande());
        
        buttonsBox.getChildren().addAll(cancelBtn, submitBtn);
        return buttonsBox;
    }
    
    private void calculateTotal() {
        double total = 0;
        for (ProduitLine line : produitLines) {
            if (line.isActive()) {
                total += line.getTotal();
            }
        }
        sousTotalLabel.setText(String.format("%,.0f FCFA", total));
        totalLabel.setText(String.format("%,.0f FCFA", total));
        
        boolean hasProduct = false;
        for (ProduitLine line : produitLines) {
            if (line.isActive() && line.getSelectedProduct() != null && line.getQuantity() > 0) {
                hasProduct = true;
                break;
            }
        }
        submitBtn.setDisable(!hasProduct);
    }
    
    private void saveCommande() {
        if (clientSelect.getValue() == null) {
            showToast("Sélectionnez un client");
            return;
        }
        
        boolean hasProduct = false;
        List<Map<String, Integer>> items = new ArrayList<>();
        
        for (ProduitLine line : produitLines) {
            if (line.isActive() && line.getSelectedProduct() != null && line.getQuantity() > 0) {
                hasProduct = true;
                Map<String, Integer> item = new HashMap<>();
                item.put("id", line.getSelectedProduct().getId());
                item.put("qty", line.getQuantity());
                items.add(item);
            }
        }
        
        if (!hasProduct) {
            showToast("Ajoutez au moins un produit");
            return;
        }
        
        int clientId = clientSelect.getValue().getId();
        LocalDate date = datePicker.getValue();
        int userId = 1; // À remplacer par l'ID de l'utilisateur connecté
        
        submitBtn.setDisable(true);
        submitBtn.setText("⏳ Enregistrement...");
        
        new Thread(() -> {
            try {
                int newId = commandeService.addCommande(clientId, userId, date, items);
                Platform.runLater(() -> {
                    AlertUtils.showSuccessMessage("Commande créée avec succès !");
                    Map<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(newId));
                    navigationManager.navigateToWithParams("commandeDetail", params);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Erreur: " + e.getMessage());
                    submitBtn.setDisable(false);
                    submitBtn.setText("✓ Valider la commande");
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showToast(String message) {
        AlertUtils.showWarningMessage(message);
    }
    
    // Classe interne pour une ligne de produit
    private class ProduitLine extends GridPane {
        private ComboBox<Produit> productSelect;
        private TextField quantityField;
        private Label totalLineLabel;
        private Label prixLabel;
        private Runnable onUpdate;
        private java.util.function.Consumer<String> toastConsumer;
        private boolean active = true;
        
        public ProduitLine(List<Produit> produits, Runnable onUpdate, java.util.function.Consumer<String> toastConsumer) {
            this.onUpdate = onUpdate;
            this.toastConsumer = toastConsumer;
            setupUI(produits);
        }
        
        private void setupUI(List<Produit> produits) {
            setHgap(10);
            setPadding(new Insets(10));
            setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 10px;");
            
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.ALWAYS);
            col1.setPercentWidth(40);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(20);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPercentWidth(15);
            ColumnConstraints col4 = new ColumnConstraints();
            col4.setPercentWidth(20);
            ColumnConstraints col5 = new ColumnConstraints();
            col5.setPercentWidth(5);
            getColumnConstraints().addAll(col1, col2, col3, col4, col5);
            
            productSelect = new ComboBox<>();
            productSelect.getItems().addAll(produits);
            productSelect.setPromptText("-- Sélectionner --");
            productSelect.setStyle("-fx-padding: 8px; -fx-background-radius: 6px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
            productSelect.setCellFactory(lv -> new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String stockInfo = item.getQuantite() <= 0 ? " ⚠️ RUPTURE" : (item.getQuantite() < 5 ? " ⚠️ Stock: " + item.getQuantite() : "");
                        setText(item.getNomp() + " - " + String.format("%,d", (int) item.getPrix()) + " FCFA" + stockInfo);
                        if (item.getQuantite() <= 0) {
                            setStyle("-fx-text-fill: #9ca3af;");
                        } else if (item.getQuantite() < 5) {
                            setStyle("-fx-text-fill: #f59e0b;");
                        } else {
                            setStyle("-fx-text-fill: #2d3748;");
                        }
                    }
                }
            });
            productSelect.setButtonCell(new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Produit" : item.getNomp());
                }
            });
            productSelect.setOnAction(e -> calculateLineTotal());
            
            prixLabel = new Label("0 FCFA");
            prixLabel.setStyle("-fx-padding: 8px; -fx-background-color: #f1f5f9; -fx-background-radius: 6px; -fx-alignment: center;");
            
            quantityField = new TextField("1");
            quantityField.setStyle("-fx-padding: 8px; -fx-alignment: center; -fx-background-radius: 6px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
            quantityField.textProperty().addListener((obs, old, val) -> calculateLineTotal());
            
            totalLineLabel = new Label("0 FCFA");
            totalLineLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981; -fx-padding: 8px; -fx-alignment: center-right;");
            
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-background-radius: 20px; -fx-cursor: hand; -fx-min-width: 32px; -fx-min-height: 32px;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-background-color: #fecaca; -fx-text-fill: #dc2626; -fx-background-radius: 20px; -fx-cursor: hand; -fx-min-width: 32px; -fx-min-height: 32px;"));
            removeBtn.setOnMouseExited(e -> removeBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-background-radius: 20px; -fx-cursor: hand; -fx-min-width: 32px; -fx-min-height: 32px;"));
            removeBtn.setOnAction(e -> {
                active = false;
                setVisible(false);
                setManaged(false);
                if (onUpdate != null) onUpdate.run();
            });
            
            add(productSelect, 0, 0);
            add(prixLabel, 1, 0);
            add(quantityField, 2, 0);
            add(totalLineLabel, 3, 0);
            add(removeBtn, 4, 0);
        }
        
        private void calculateLineTotal() {
            Produit p = productSelect.getValue();
            if (p != null) {
                int stock = p.getQuantite();
                int qty = 1;
                try {
                    qty = Integer.parseInt(quantityField.getText());
                    if (qty > stock && stock > 0) {
                        qty = stock;
                        quantityField.setText(String.valueOf(stock));
                        if (toastConsumer != null) toastConsumer.accept("Stock limité à " + stock);
                    }
                    if (stock <= 0 && qty > 0) {
                        qty = 0;
                        quantityField.setText("0");
                        if (toastConsumer != null) toastConsumer.accept("Produit en rupture");
                    }
                } catch (NumberFormatException e) {
                    qty = 0;
                }
                
                double prix = p.getPrix();
                double total = prix * qty;
                
                prixLabel.setText(String.format("%,.0f FCFA", prix));
                totalLineLabel.setText(String.format("%,.0f FCFA", total));
            } else {
                prixLabel.setText("0 FCFA");
                totalLineLabel.setText("0 FCFA");
            }
            if (onUpdate != null) onUpdate.run();
        }
        
        public Produit getSelectedProduct() { return productSelect.getValue(); }
        public int getQuantity() { 
            try { return Integer.parseInt(quantityField.getText()); } 
            catch (NumberFormatException e) { return 0; }
        }
        public double getTotal() {
            Produit p = productSelect.getValue();
            if (p == null) return 0;
            return p.getPrix() * getQuantity();
        }
        public boolean isActive() { return active && getSelectedProduct() != null && getQuantity() > 0; }
    }
}