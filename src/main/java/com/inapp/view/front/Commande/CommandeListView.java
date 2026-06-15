package com.inapp.view.front.commande;

import javafx.animation.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Commande;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommandeListView extends VBox {
    
    private NavigationManager navigationManager;
    private TableView<Commande> tableView;
    private ObservableList<Commande> commandeList;
    private FilteredList<Commande> filteredList;
    private Font fontAwesome;
    
    // Statistiques
    private Label totalCountLabel;
    private Label pendingCountLabel;
    private Label deliveredCountLabel;
    private Label cancelledCountLabel;
    
    // Filtres
    private TextField searchField;
    private DatePicker dateFilter;
    private String currentFilter = "all";
    private ToggleGroup filterToggleGroup;
    
    // Pagination
    private int currentPage = 1;
    private int rowsPerPage = 5;
    private HBox paginationBox;
    
    // Selection multiple
    private CheckBox selectAllCheckbox;
    private HBox bulkBar;
    private Label selectedCountLabel;
    private ComboBox<String> bulkStatusCombo;
    private List<Integer> selectedIds = new ArrayList<>();
    
    public CommandeListView(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.commandeList = FXCollections.observableArrayList();
        loadFontAwesome();
        setupUI();
        loadData();
        setupEventHandlers();
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
    
    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #f8f9fa;");
        
        filteredList = new FilteredList<>(commandeList, p -> true);
        
        getChildren().add(createHeader());
        getChildren().add(createStatsCards());
        getChildren().add(createFilterBar());
        getChildren().add(createTableCard());
        
        paginationBox = createPagination();
        getChildren().add(paginationBox);
        
        bulkBar = createBulkBar();
        getChildren().add(bulkBar);
        
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
        header.setPadding(new Insets(0, 0, 20, 0));
        
        VBox titleBox = new VBox(5);
        HBox titleIcon = new HBox(10);
        titleIcon.setAlignment(Pos.CENTER_LEFT);
        
        Text cartIcon = createIcon("\uF07A", "#E66239", 24); // fa-shopping-cart
        Label titleText = new Label("Commandes");
        titleText.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        titleIcon.getChildren().addAll(cartIcon, titleText);
        
        Label subtitle = new Label("Gérez vos commandes et suivez leur statut");
        subtitle.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titleIcon, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox actionsBox = new HBox(10);
        
        Button exportBtn = new Button();
        Text exportIcon = createIcon("\uF56D", "#4a5568", 14); // fa-download
        exportBtn.setGraphic(exportIcon);
        exportBtn.setText(" Exporter");
        exportBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 8px 16px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500;");
        exportBtn.setOnAction(e -> exportToCSV());
        
        Button addBtn = new Button();
        Text addIcon = createIcon("\uF067", "white", 14); // fa-plus
        addBtn.setGraphic(addIcon);
        addBtn.setText(" Nouvelle commande");
        addBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 8px 20px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand;");
        addBtn.setOnAction(e -> navigationManager.navigateTo("commandeAdd"));
        
        actionsBox.getChildren().addAll(exportBtn, addBtn);
        
        header.getChildren().addAll(titleBox, spacer, actionsBox);
        return header;
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(20);
        
        totalCountLabel = new Label("0");
        pendingCountLabel = new Label("0");
        deliveredCountLabel = new Label("0");
        cancelledCountLabel = new Label("0");
        
        VBox card1 = createStatCard("\uF07A", "Total commandes", totalCountLabel, "#667eea", "all"); // fa-shopping-cart
        VBox card2 = createStatCard("\uF017", "En attente", pendingCountLabel, "#f59e0b", "en_attente"); // fa-clock
        VBox card3 = createStatCard("\uF0D1", "Livrées", deliveredCountLabel, "#10b981", "livree"); // fa-truck
        VBox card4 = createStatCard("\uF00D", "Annulées", cancelledCountLabel, "#ef4444", "annulee"); // fa-times
        
        statsBox.getChildren().addAll(card1, card2, card3, card4);
        for (int i = 0; i < 4; i++) {
            HBox.setHgrow(statsBox.getChildren().get(i), Priority.ALWAYS);
        }
        
        return statsBox;
    }
    
    private VBox createStatCard(String iconCode, String title, Label valueLabel, String color, String filterValue) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> {
            currentFilter = filterValue;
            updateFilterTabs();
            filterData();
            currentPage = 1;
            updateTablePage();
        });
        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text icon = createIcon(iconCode, color, 22);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        header.getChildren().addAll(icon, spacer, titleLabel);
        
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(header, valueLabel);
        return card;
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(15, 20, 15, 20));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
        
        HBox filterTabs = new HBox(10);
        filterToggleGroup = new ToggleGroup();
        
        String[][] filters = {{"Toutes", "all"}, {"En attente", "en_attente"}, {"Livrées", "livree"}, {"Annulées", "annulee"}};
        for (String[] f : filters) {
            ToggleButton tab = new ToggleButton(f[0]);
            tab.setUserData(f[1]);
            tab.setToggleGroup(filterToggleGroup);
            tab.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 6px 16px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: 500; -fx-cursor: hand;");
            tab.selectedProperty().addListener((obs, old, val) -> {
                if (val) {
                    currentFilter = (String) tab.getUserData();
                    currentPage = 1;
                    filterData();
                }
            });
            filterTabs.getChildren().add(tab);
        }
        ((ToggleButton) filterTabs.getChildren().get(0)).setSelected(true);
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 40px; -fx-padding: 4px 16px;");
        Text searchIcon = createIcon("\uF002", "#718096", 14); // fa-search
        searchField = new TextField();
        searchField.setPromptText("Client ou N° commande...");
        searchField.setStyle("-fx-background-color: transparent; -fx-padding: 8px;");
        searchField.setPrefWidth(220);
        searchField.textProperty().addListener((obs, old, val) -> {
            currentPage = 1;
            filterData();
        });
        searchBox.getChildren().addAll(searchIcon, searchField);
        
        dateFilter = new DatePicker();
        dateFilter.setPromptText("Filtrer par date");
        dateFilter.setStyle("-fx-background-radius: 20px;");
        dateFilter.valueProperty().addListener((obs, old, val) -> {
            currentPage = 1;
            filterData();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        filterBar.getChildren().addAll(filterTabs, spacer, searchBox, dateFilter);
        return filterBar;
    }
    
    private void updateFilterTabs() {
        if (filterToggleGroup != null) {
            for (Toggle toggle : filterToggleGroup.getToggles()) {
                ToggleButton tab = (ToggleButton) toggle;
                if (tab.getUserData().equals(currentFilter)) {
                    tab.setSelected(true);
                    break;
                }
            }
        }
    }
    
    private VBox createTableCard() {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 10, 20));
        selectAllCheckbox = new CheckBox("Tout sélectionner");
        selectAllCheckbox.setStyle("-fx-font-size: 12px;");
        selectAllCheckbox.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                for (Commande cmd : tableView.getItems()) {
                    if (!selectedIds.contains(cmd.getId())) selectedIds.add(cmd.getId());
                }
            } else {
                selectedIds.clear();
            }
            updateBulkBar();
            tableView.refresh();
        });
        header.getChildren().add(selectAllCheckbox);
        
        tableView = new TableView<>();
        tableView.setStyle("-fx-border-color: transparent;");
        
        TableColumn<Commande, Boolean> checkCol = new TableColumn<>("");
        checkCol.setPrefWidth(40);
        checkCol.setCellFactory(col -> new TableCell<Commande, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(selectedIds.contains(cmd.getId()));
                    checkBox.selectedProperty().addListener((obs, old, val) -> {
                        if (val) {
                            selectedIds.add(cmd.getId());
                        } else {
                            selectedIds.remove((Integer) cmd.getId());
                        }
                        updateBulkBar();
                        selectAllCheckbox.setSelected(selectedIds.size() == tableView.getItems().size() && !tableView.getItems().isEmpty());
                    });
                    setGraphic(checkBox);
                }
            }
        });
        
        TableColumn<Commande, Integer> idCol = new TableColumn<>("N° Commande");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        idCol.setCellFactory(col -> new TableCell<Commande, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else {
                    setText("#" + item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #E66239;");
                }
            }
        });
        
        TableColumn<Commande, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientCol.setPrefWidth(180);
        clientCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else {
                    setText(item);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
        
        TableColumn<Commande, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        dateCol.setPrefWidth(120);
        
        TableColumn<Commande, String> produitsCol = new TableColumn<>("Produits");
        produitsCol.setCellValueFactory(new PropertyValueFactory<>("produits"));
        produitsCol.setPrefWidth(250);
        produitsCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    VBox container = new VBox(5);
                    container.setAlignment(Pos.CENTER_LEFT);
                    String[] produits = item.split(",");
                    for (String p : produits) {
                        Label tag = new Label(p.trim());
                        tag.setStyle("-fx-background-color: #f1f5f9; -fx-font-size: 11px; -fx-padding: 2px 8px; -fx-background-radius: 4px;");
                        container.getChildren().add(tag);
                    }
                    setGraphic(container);
                }
            }
        });
        
        TableColumn<Commande, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        totalCol.setPrefWidth(120);
        totalCol.setCellFactory(col -> new TableCell<Commande, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else {
                    setText(String.format("%,.0f FCFA", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #E66239;");
                }
            }
        });
        
        TableColumn<Commande, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statusCol.setPrefWidth(180);
        statusCol.setCellFactory(col -> new TableCell<Commande, String>() {
            private final ComboBox<String> statusCombo = new ComboBox<>();
            {
                statusCombo.getItems().addAll("", "En attente", "Livrée", "Annulée");
                statusCombo.setPromptText("Changer");
                statusCombo.setStyle("-fx-font-size: 11px; -fx-padding: 2px; -fx-background-radius: 5px;");
                statusCombo.setOnAction(e -> {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    String newStatus = statusCombo.getValue();
                    if (newStatus != null && !newStatus.isEmpty()) {
                        String statusKey = "";
                        if (newStatus.equals("En attente")) statusKey = "en_attente";
                        else if (newStatus.equals("Livrée")) statusKey = "livree";
                        else statusKey = "annulee";
                        updateCommandeStatus(cmd.getId(), statusKey);
                        statusCombo.setValue("");
                    }
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    Label badge = new Label();
                    if ("en_attente".equals(item)) {
                        badge.setText("⏳ En attente");
                        badge.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 30px; -fx-font-size: 11px;");
                    } else if ("livree".equals(item)) {
                        badge.setText("✅ Livrée");
                        badge.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 30px; -fx-font-size: 11px;");
                    } else {
                        badge.setText("❌ Annulée");
                        badge.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 30px; -fx-font-size: 11px;");
                    }
                    
                    container.getChildren().addAll(badge, statusCombo);
                    setGraphic(container);
                }
            }
        });
        
        TableColumn<Commande, String> factureCol = new TableColumn<>("Facture");
        factureCol.setPrefWidth(100);
        factureCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    if ("livree".equals(cmd.getStatut())) {
                        Hyperlink factureLink = new Hyperlink("📄 Facture");
                        factureLink.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px; -fx-underline: true; -fx-cursor: hand;");
                        factureLink.setOnAction(e -> {
                            Map<String, String> params = new HashMap<>();
                            params.put("id", String.valueOf(cmd.getId()));
                            navigationManager.navigateToWithParams("factureDetails", params);
                        });
                        setGraphic(factureLink);
                    } else {
                        Label noFacture = new Label("-");
                        noFacture.setStyle("-fx-text-fill: #999;");
                        setGraphic(noFacture);
                    }
                }
            }
        });
        
        // ========== ACTIONS AVEC ICÔNES FONTAWESOME ==========
        TableColumn<Commande, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(140);
        actionsCol.setStyle("-fx-alignment: CENTER;");
        actionsCol.setCellFactory(col -> new TableCell<Commande, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(8);
                    buttons.setAlignment(Pos.CENTER);
                    
                    // Bouton Voir - fa-eye
                    Button viewBtn = new Button();
                    Text viewIcon = createIcon("\uF06E", "white", 14);
                    viewBtn.setGraphic(viewIcon);
                    viewBtn.setStyle("-fx-background-color: #E66239; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;");
                    viewBtn.setOnMouseEntered(e -> viewBtn.setStyle("-fx-background-color: #d5542e; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;"));
                    viewBtn.setOnMouseExited(e -> viewBtn.setStyle("-fx-background-color: #E66239; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;"));
                    Tooltip.install(viewBtn, new Tooltip("Voir la commande"));
                    viewBtn.setOnAction(e -> {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", String.valueOf(cmd.getId()));
                        navigationManager.navigateToWithParams("commandeDetails", params);
                    });
                    
                    // Bouton Modifier - fa-pen
                    Button editBtn = new Button();
                    Text editIcon = createIcon("\uF304", "#333", 14);
                    editBtn.setGraphic(editIcon);
                    editBtn.setStyle("-fx-background-color: #ffc107; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;");
                    editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #e0a800; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;"));
                    editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #ffc107; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;"));
                    Tooltip.install(editBtn, new Tooltip("Modifier la commande"));
                    editBtn.setOnAction(e -> {
                        if ("livree".equals(cmd.getStatut())) {
                            AlertUtils.showWarningMessage("Impossible de modifier une commande livrée");
                        } else {
                            Map<String, String> params = new HashMap<>();
                            params.put("id", String.valueOf(cmd.getId()));
                            navigationManager.navigateToWithParams("commandeEdit", params);
                        }
                    });
                    
                    // Bouton Supprimer - fa-trash
                    Button deleteBtn = new Button();
                    Text deleteIcon = createIcon("\uF1F8", "white", 14);
                    deleteBtn.setGraphic(deleteIcon);
                    deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;");
                    deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #c82333; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;"));
                    deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 8px; -fx-cursor: hand;"));
                    Tooltip.install(deleteBtn, new Tooltip("Supprimer la commande"));
                    deleteBtn.setOnAction(e -> {
                        if (AlertUtils.confirmDelete("Supprimer la commande #" + cmd.getId() + " ?")) {
                            commandeList.remove(cmd);
                            updateStats();
                            updateTablePage();
                            AlertUtils.showSuccessMessage("Commande supprimée");
                        }
                    });
                    
                    buttons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        tableView.getColumns().addAll(checkCol, idCol, clientCol, dateCol, produitsCol, totalCol, statusCol, factureCol, actionsCol);
        tableView.setItems(filteredList);
        
        card.getChildren().addAll(header, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        return card;
    }
    
    private HBox createPagination() {
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);
        pagination.setPadding(new Insets(20, 0, 0, 0));
        return pagination;
    }
    
    private HBox createBulkBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 40px;");
        bar.setVisible(false);
        
        Text iconText = createIcon("\uF0CB", "white", 14); // fa-list
        iconText.setStyle(iconText.getStyle() + " -fx-fill: white;");
        
        selectedCountLabel = new Label("0");
        selectedCountLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label textLabel = new Label("sélectionnée(s)");
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        
        bulkStatusCombo = new ComboBox<>();
        bulkStatusCombo.getItems().addAll("Changer statut", "En attente", "Livrée", "Annulée");
        bulkStatusCombo.setValue("Changer statut");
        bulkStatusCombo.setStyle("-fx-background-radius: 20px; -fx-font-size: 12px;");
        bulkStatusCombo.setOnAction(e -> {
            String val = bulkStatusCombo.getValue();
            if (!val.equals("Changer statut") && !selectedIds.isEmpty()) {
                String statusKey = val.equals("En attente") ? "en_attente" : 
                                  (val.equals("Livrée") ? "livree" : "annulee");
                for (int id : selectedIds) {
                    updateCommandeStatus(id, statusKey);
                }
                AlertUtils.showSuccessMessage(selectedIds.size() + " commande(s) mise(s) à jour");
                bulkStatusCombo.setValue("Changer statut");
                clearSelection();
            }
        });
        
        Button deleteBtn = new Button();
        Text deleteIcon = createIcon("\uF1F8", "white", 13);
        deleteBtn.setGraphic(deleteIcon);
        deleteBtn.setText(" Supprimer");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 6px 16px; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            if (AlertUtils.confirmDelete("Supprimer " + selectedIds.size() + " commande(s) ?")) {
                for (int id : selectedIds) {
                    commandeList.removeIf(cmd -> cmd.getId() == id);
                }
                updateStats();
                updateTablePage();
                AlertUtils.showSuccessMessage(selectedIds.size() + " commande(s) supprimée(s)");
                clearSelection();
            }
        });
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> clearSelection());
        
        bar.getChildren().addAll(iconText, selectedCountLabel, textLabel, bulkStatusCombo, deleteBtn, closeBtn);
        return bar;
    }
    
    private void updateCommandeStatus(int id, String newStatus) {
        for (Commande cmd : commandeList) {
            if (cmd.getId() == id) {
                cmd.setStatut(newStatus);
                break;
            }
        }
        updateStats();
        tableView.refresh();
    }
    
    private void clearSelection() {
        selectedIds.clear();
        selectAllCheckbox.setSelected(false);
        updateBulkBar();
        tableView.refresh();
    }
    
    private void loadData() {
        commandeList.addAll(
            new Commande(1, "Jean Dupont", "2025-06-10", "en_attente", 250000, "Réfrigérateur Samsung (2), Lave-linge LG (1)"),
            new Commande(2, "Marie Martin", "2025-06-09", "livree", 180000, "Climatiseur Haier (1)"),
            new Commande(3, "Pierre Durand", "2025-06-08", "annulee", 95000, "Micro-ondes Panasonic (1)"),
            new Commande(4, "Sophie Bernard", "2025-06-07", "en_attente", 320000, "Lave-linge LG (1), Réfrigérateur Samsung (1)"),
            new Commande(5, "Lucas Petit", "2025-06-06", "livree", 150000, "Cuisinière Whirlpool (1)"),
            new Commande(6, "Emma Richard", "2025-06-05", "en_attente", 89000, "Micro-ondes Panasonic (1), Grille-pain (1)"),
            new Commande(7, "Thomas Dubois", "2025-06-04", "annulee", 250000, "Réfrigérateur Samsung (1)"),
            new Commande(8, "Julie Moreau", "2025-06-03", "livree", 450000, "Lave-linge LG (2), Climatiseur Haier (1)")
        );
        
        tableView.setItems(filteredList);
        updateStats();
        updateTablePage();
    }
    
    private void filterData() {
        if (searchField == null) return;
        
        String search = searchField.getText().toLowerCase();
        LocalDate date = dateFilter.getValue();
        
        filteredList.setPredicate(cmd -> {
            if (!"all".equals(currentFilter) && !cmd.getStatut().equals(currentFilter)) return false;
            if (!search.isEmpty() && !cmd.getClientName().toLowerCase().contains(search) && 
                !String.valueOf(cmd.getId()).contains(search)) return false;
            if (date != null && !cmd.getDateCommande().equals(date.toString())) return false;
            return true;
        });
        
        updateStats();
        updateTablePage();
    }
    
    private void updateStats() {
        int total = filteredList.size();
        int pending = (int) filteredList.stream().filter(c -> "en_attente".equals(c.getStatut())).count();
        int delivered = (int) filteredList.stream().filter(c -> "livree".equals(c.getStatut())).count();
        int cancelled = (int) filteredList.stream().filter(c -> "annulee".equals(c.getStatut())).count();
        
        totalCountLabel.setText(String.valueOf(total));
        pendingCountLabel.setText(String.valueOf(pending));
        deliveredCountLabel.setText(String.valueOf(delivered));
        cancelledCountLabel.setText(String.valueOf(cancelled));
    }
    
    private void updateTablePage() {
        if (paginationBox == null) return;
        
        paginationBox.getChildren().clear();
        int totalPages = (int) Math.ceil(filteredList.size() / (double) rowsPerPage);
        
        if (totalPages <= 1) {
            tableView.setItems(filteredList);
            return;
        }
        
        currentPage = Math.max(1, Math.min(currentPage, totalPages));
        
        Button prevBtn = new Button("◀");
        prevBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand; -fx-font-size: 12px;");
        prevBtn.setOnMouseEntered(e -> prevBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #E66239; -fx-cursor: hand; -fx-font-size: 12px;"));
        prevBtn.setOnMouseExited(e -> prevBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-cursor: hand; -fx-font-size: 12px;"));
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTablePage();
            }
        });
        
        HBox pagesBox = new HBox(5);
        pagesBox.setAlignment(Pos.CENTER);
        
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        
        for (int i = startPage; i <= endPage; i++) {
            int pageNum = i;
            Button pageBtn = new Button(String.valueOf(i));
            if (currentPage == i) {
                pageBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #E66239; -fx-cursor: hand; -fx-font-weight: bold;");
            } else {
                pageBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand; -fx-font-weight: 500;");
                pageBtn.setOnMouseEntered(e -> pageBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #E66239; -fx-cursor: hand; -fx-font-weight: bold;"));
                pageBtn.setOnMouseExited(e -> pageBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-cursor: hand; -fx-font-weight: 500;"));
            }
            pageBtn.setOnAction(e -> {
                currentPage = pageNum;
                updateTablePage();
            });
            pagesBox.getChildren().add(pageBtn);
        }
        
        Button nextBtn = new Button("▶");
        nextBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand; -fx-font-size: 12px;");
        nextBtn.setOnMouseEntered(e -> nextBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #E66239; -fx-cursor: hand; -fx-font-size: 12px;"));
        nextBtn.setOnMouseExited(e -> nextBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-cursor: hand; -fx-font-size: 12px;"));
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTablePage();
            }
        });
        
        paginationBox.getChildren().addAll(prevBtn, pagesBox, nextBtn);
        
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredList.size());
        if (start < filteredList.size() && start >= 0) {
            List<Commande> subList = new ArrayList<>(filteredList.subList(start, end));
            tableView.setItems(FXCollections.observableArrayList(subList));
        }
    }
    
    private void updateBulkBar() {
        boolean show = selectedIds.size() >= 2;
        bulkBar.setVisible(show);
        if (show) {
            selectedCountLabel.setText(String.valueOf(selectedIds.size()));
        }
    }
    
    private void setupEventHandlers() {
        // Les écouteurs sont déjà définis
    }
    
    private void exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("N° Commande;Client;Date;Produits;Montant;Statut\n");
        
        for (Commande cmd : commandeList) {
            csv.append(cmd.getId()).append(";");
            csv.append(cmd.getClientName()).append(";");
            csv.append(cmd.getDateCommande()).append(";");
            csv.append("\"").append(cmd.getProduits() != null ? cmd.getProduits() : "").append("\";");
            csv.append(cmd.getMontantTotal()).append(";");
            csv.append(cmd.getStatut()).append("\n");
        }
        
        AlertUtils.showInfoMessage("Export CSV lancé - " + commandeList.size() + " commandes exportées");
    }
}