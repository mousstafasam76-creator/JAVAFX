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
import com.inapp.model.Client;
import com.inapp.model.Produit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Edit extends VBox {
    
    private NavigationManager navigationManager;
    private int commandeId;
    
    // Formulaire
    private ComboBox<Client> clientSelect;
    private DatePicker datePicker;
    private VBox produitsContainer;
    private List<EditProduitLine> produitLines;
    private Label totalLabel;
    private Button submitBtn;
    
    // Données mock
    private List<Client> clients;
    private List<Produit> produits;
    private Commande commande;
    
    public Edit(NavigationManager navManager, int id) {
        this.navigationManager = navManager;
        this.commandeId = id;
        this.produitLines = new ArrayList<>();
        loadMockData();
        setupUI();
        loadCommandeData();
    }
    
    private void loadMockData() {
        clients = new ArrayList<>();
        clients.add(new Client(1, "Jean", "Dupont", "771234567", "jean@email.com", "Dakar"));
        clients.add(new Client(2, "Marie", "Martin", "772345678", "marie@email.com", "Thiès"));
        clients.add(new Client(3, "Pierre", "Durand", "773456789", "pierre@email.com", "Mbour"));
        
        produits = new ArrayList<>();
        produits.add(new Produit(1, "Réfrigérateur Samsung", 250000, 10));
        produits.add(new Produit(2, "Lave-linge LG", 180000, 8));
        produits.add(new Produit(3, "Climatiseur Haier", 150000, 5));
        produits.add(new Produit(4, "Micro-ondes Panasonic", 95000, 7));
        produits.add(new Produit(5, "Cuisinière Whirlpool", 120000, 4));
        
        commande = new Commande(commandeId, "Jean Dupont", LocalDate.now().toString(), "en_attente", 250000, "Produit A (2), Produit B (1)");
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
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 16px;");
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
        Label title = new Label("✏️ Modifier commande #" + commandeId);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label subtitle = new Label("Modifiez les informations de la commande");
        subtitle.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backBtn = new Button("← Retour");
        backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigationManager.navigateTo("commandesList"));
        
        header.getChildren().addAll(titleBox, spacer, backBtn);
        return header;
    }
    
    private VBox createClientSection() {
        VBox section = new VBox(8);
        Label label = new Label("Client *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        
        clientSelect = new ComboBox<>();
        clientSelect.getItems().addAll(clients);
        clientSelect.setPromptText("Sélectionner un client...");
        clientSelect.setStyle("-fx-padding: 10px; -fx-background-radius: 8px;");
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
        
        section.getChildren().addAll(label, clientSelect);
        return section;
    }
    
    private VBox createDateSection() {
        VBox section = new VBox(8);
        Label label = new Label("Date de commande *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-padding: 10px; -fx-background-radius: 8px;");
        
        section.getChildren().addAll(label, datePicker);
        return section;
    }
    
    private VBox createProduitsSection() {
        VBox section = new VBox(10);
        Label label = new Label("Produits commandés *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        
        produitsContainer = new VBox(10);
        produitsContainer.setPadding(new Insets(10, 0, 10, 0));
        
        addProduitLine();
        
        Button addProductBtn = new Button("+ Ajouter un produit");
        addProductBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
        addProductBtn.setOnAction(e -> addProduitLine());
        
        section.getChildren().addAll(label, produitsContainer, addProductBtn);
        return section;
    }
    
    private void addProduitLine() {
        EditProduitLine line = new EditProduitLine(produits, this::calculateTotal);
        produitLines.add(line);
        produitsContainer.getChildren().add(line);
    }
    
    private HBox createTotalSection() {
        HBox section = new HBox();
        section.setAlignment(Pos.CENTER_RIGHT);
        section.setPadding(new Insets(15, 0, 0, 0));
        
        VBox totalBox = new VBox(10);
        totalBox.setStyle("-fx-background-color: #f8fafc; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        totalBox.setPadding(new Insets(20));
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        
        HBox sousTotalBox = new HBox(20);
        sousTotalBox.setAlignment(Pos.CENTER_RIGHT);
        Label sousTotalLabel = new Label("Sous-total :");
        sousTotalLabel.setStyle("-fx-text-fill: #666;");
        Label sousTotalValue = new Label("0 FCFA");
        sousTotalValue.setStyle("-fx-font-weight: bold;");
        sousTotalBox.getChildren().addAll(sousTotalLabel, sousTotalValue);
        
        Separator sep = new Separator();
        sep.setPrefWidth(200);
        
        HBox totalBoxRow = new HBox(20);
        totalBoxRow.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabelText = new Label("TOTAL");
        totalLabelText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        totalLabel = new Label("0 FCFA");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #E66239;");
        totalBoxRow.getChildren().addAll(totalLabelText, totalLabel);
        
        totalBox.getChildren().addAll(sousTotalBox, sep, totalBoxRow);
        section.getChildren().add(totalBox);
        
        return section;
    }
    
    private HBox createButtonsSection() {
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setPadding(new Insets(15, 0, 0, 0));
        
        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> navigationManager.navigateTo("commandesList"));
        
        submitBtn = new Button("Enregistrer");
        submitBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 10px 25px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: bold;");
        submitBtn.setOnAction(e -> updateCommande());
        
        buttonsBox.getChildren().addAll(cancelBtn, submitBtn);
        return buttonsBox;
    }
    
    private void calculateTotal() {
        double total = 0;
        for (EditProduitLine line : produitLines) {
            total += line.getTotal();
        }
        totalLabel.setText(String.format("%,.0f FCFA", total));
    }
    
    private void loadCommandeData() {
        // Simuler chargement des données existantes
        clientSelect.setValue(clients.get(0));
        datePicker.setValue(LocalDate.now());
        
        // Ajouter des produits existants
        EditProduitLine line = new EditProduitLine(produits, this::calculateTotal);
        line.setProduct(produits.get(0), 2);
        produitLines.add(line);
        produitsContainer.getChildren().add(line);
        
        calculateTotal();
    }
    
    private void updateCommande() {
        if (clientSelect.getValue() == null) {
            AlertUtils.showErrorMessage("Veuillez sélectionner un client");
            return;
        }
        
        AlertUtils.showSuccessMessage("Commande modifiée avec succès !");
        navigationManager.navigateTo("commandesList");
    }
    
    // Classe interne pour une ligne de produit
    private class EditProduitLine extends HBox {
        private ComboBox<Produit> productSelect;
        private TextField quantityField;
        private Label totalLineLabel;
        private Runnable onUpdate;
        
        public EditProduitLine(List<Produit> produits, Runnable onUpdate) {
            this.onUpdate = onUpdate;
            setSpacing(10);
            setAlignment(Pos.CENTER_LEFT);
            setPadding(new Insets(5, 0, 5, 0));
            setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-padding: 10px;");
            
            productSelect = new ComboBox<>();
            productSelect.getItems().addAll(produits);
            productSelect.setPromptText("Produit");
            productSelect.setPrefWidth(250);
            productSelect.setCellFactory(lv -> new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNomp() + " - " + String.format("%,.0f", item.getPrix()) + " FCFA");
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
            
            quantityField = new TextField("1");
            quantityField.setPrefWidth(80);
            quantityField.setStyle("-fx-alignment: center;");
            quantityField.textProperty().addListener((obs, old, val) -> calculateLineTotal());
            
            totalLineLabel = new Label("0 FCFA");
            totalLineLabel.setPrefWidth(120);
            totalLineLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");
            
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-background-radius: 20px; -fx-cursor: hand;");
            removeBtn.setOnAction(e -> {
                getChildren().clear();
                setVisible(false);
                if (onUpdate != null) onUpdate.run();
            });
            
            getChildren().addAll(productSelect, quantityField, totalLineLabel, removeBtn);
        }
        
        public void setProduct(Produit p, int qty) {
            productSelect.setValue(p);
            quantityField.setText(String.valueOf(qty));
            calculateLineTotal();
        }
        
        private void calculateLineTotal() {
            Produit p = productSelect.getValue();
            if (p != null) {
                try {
                    int qty = Integer.parseInt(quantityField.getText());
                    double total = p.getPrix() * qty;
                    totalLineLabel.setText(String.format("%,.0f FCFA", total));
                } catch (NumberFormatException e) {
                    totalLineLabel.setText("0 FCFA");
                }
            } else {
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
    }
}