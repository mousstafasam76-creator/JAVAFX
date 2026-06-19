package com.inapp.view.front.commande;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Commande;
import com.inapp.service.CommandeService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommandeListView extends VBox {
    
    private NavigationManager navigationManager;
    private CommandeService commandeService;
    private TableView<Commande> tableView;
    private ObservableList<Commande> commandeList;
    private FilteredList<Commande> filteredList;
    
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
    private int rowsPerPage = 15;
    private HBox paginationBox;
    
    // Selection multiple
    private CheckBox selectAllCheckbox;
    private HBox bulkBar;
    private Label selectedCountLabel;
    private ComboBox<String> bulkStatusCombo;
    private List<Integer> selectedIds = new ArrayList<>();
    
    public CommandeListView(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.commandeService = new CommandeService();
        this.commandeList = FXCollections.observableArrayList();
        setupUI();
        loadData();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(15);
        setStyle("-fx-background-color: #f8f9fa;");
        
        filteredList = new FilteredList<>(commandeList, p -> true);
        
        getChildren().add(createHeader());
        getChildren().add(createStatsCards());
        getChildren().add(createFilterBar());
        
        VBox tableCard = createTableCard();
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        getChildren().add(tableCard);
        
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
        header.setPadding(new Insets(0, 0, 10, 0));
        
        VBox titleBox = new VBox(5);
        Label title = new Label("📋 Gestion des Commandes");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label subtitle = new Label("Gérez et suivez toutes vos commandes");
        subtitle.setStyle("-fx-text-fill: #718096; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox actionsBox = new HBox(10);
        
        Button exportBtn = new Button("📥 Exporter CSV");
        exportBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-padding: 10px 18px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 8px;");
        exportBtn.setOnAction(e -> exportToCSV());
        addHoverEffect(exportBtn, "white", "#f7fafc");
        
        Button addBtn = new Button("➕ Nouvelle commande");
        addBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 10px 22px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand;");
        addBtn.setOnAction(e -> navigationManager.navigateTo("commandeAdd"));
        addHoverEffect(addBtn, "#E66239", "#c5542e");
        
        actionsBox.getChildren().addAll(exportBtn, addBtn);
        
        header.getChildren().addAll(titleBox, spacer, actionsBox);
        return header;
    }
    
    private void addHoverEffect(Button btn, String defaultBg, String hoverBg) {
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(defaultBg, hoverBg)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(hoverBg, defaultBg)));
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(15);
        statsBox.setMinHeight(80);
        
        totalCountLabel = new Label("0");
        pendingCountLabel = new Label("0");
        deliveredCountLabel = new Label("0");
        cancelledCountLabel = new Label("0");
        
        VBox card1 = createStatCard("📦", "Total", totalCountLabel, "#667eea", "all");
        VBox card2 = createStatCard("⏳", "En attente", pendingCountLabel, "#f59e0b", "en_attente");
        VBox card3 = createStatCard("✅", "Livrées", deliveredCountLabel, "#10b981", "livree");
        VBox card4 = createStatCard("❌", "Annulées", cancelledCountLabel, "#ef4444", "annulee");
        
        statsBox.getChildren().addAll(card1, card2, card3, card4);
        for (int i = 0; i < 4; i++) {
            HBox.setHgrow(statsBox.getChildren().get(i), Priority.ALWAYS);
        }
        
        return statsBox;
    }
    
    private VBox createStatCard(String icon, String title, Label valueLabel, String color, String filterValue) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setMinHeight(75);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-cursor: hand; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        card.setOnMouseClicked(e -> {
            currentFilter = filterValue;
            updateFilterTabs();
            filterData();
            currentPage = 1;
            updateTablePage();
        });
        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: 500;");
        
        header.getChildren().addAll(iconLabel, spacer, titleLabel);
        
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(header, valueLabel);
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-cursor: hand; -fx-border-color: " + color + "; -fx-border-width: 2px;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-cursor: hand; -fx-border-color: #e2e8f0; -fx-border-width: 1px;"));
        
        return card;
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(12, 18, 12, 18));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        
        HBox filterTabs = new HBox(8);
        filterToggleGroup = new ToggleGroup();
        
        String[][] filters = {{"📋 Toutes", "all"}, {"⏳ En attente", "en_attente"}, {"✅ Livrées", "livree"}, {"❌ Annulées", "annulee"}};
        for (String[] f : filters) {
            ToggleButton tab = new ToggleButton(f[0]);
            tab.setUserData(f[1]);
            tab.setToggleGroup(filterToggleGroup);
            tab.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 6px 14px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-font-weight: 500; -fx-cursor: hand;");
            tab.selectedProperty().addListener((obs, old, val) -> {
                if (val) {
                    tab.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-padding: 6px 14px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-font-weight: 500;");
                    currentFilter = (String) tab.getUserData();
                    currentPage = 1;
                    filterData();
                } else {
                    tab.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #4a5568; -fx-padding: 6px 14px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-font-weight: 500;");
                }
            });
            filterTabs.getChildren().add(tab);
        }
        ((ToggleButton) filterTabs.getChildren().get(0)).setSelected(true);
        
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 30px; -fx-padding: 4px 14px;");
        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 14px;");
        searchField = new TextField();
        searchField.setPromptText("Rechercher client ou N° commande...");
        searchField.setStyle("-fx-background-color: transparent; -fx-padding: 6px; -fx-font-size: 12px;");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, old, val) -> {
            currentPage = 1;
            filterData();
        });
        searchBox.getChildren().addAll(searchIcon, searchField);
        
        dateFilter = new DatePicker();
        dateFilter.setPromptText("Filtrer par date");
        dateFilter.setStyle("-fx-background-radius: 20px; -fx-font-size: 12px;");
        dateFilter.setPrefWidth(140);
        dateFilter.valueProperty().addListener((obs, old, val) -> {
            currentPage = 1;
            filterData();
        });
        
        Button clearBtn = new Button("✕ Réinitialiser");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: 500;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            dateFilter.setValue(null);
            currentFilter = "all";
            updateFilterTabs();
            ((ToggleButton) ((HBox) filterBar.getChildren().get(0)).getChildren().get(0)).setSelected(true);
            filterData();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        filterBar.getChildren().addAll(filterTabs, spacer, searchBox, dateFilter, clearBtn);
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
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 18, 8, 18));
        header.setStyle("-fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1px;");
        
        selectAllCheckbox = new CheckBox("Sélectionner tout");
        selectAllCheckbox.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");
        selectAllCheckbox.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                selectedIds.clear();
                for (Commande cmd : tableView.getItems()) {
                    selectedIds.add(cmd.getId());
                }
            } else {
                selectedIds.clear();
            }
            updateBulkBar();
            tableView.refresh();
        });
        
        Label countLabel = new Label("0 commande(s) trouvée(s)");
        countLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #a0aec0;");
        
        commandeList.addListener((ListChangeListener<Commande>) change -> {
            Platform.runLater(() -> {
                countLabel.setText(filteredList.size() + " commande(s) trouvée(s)");
            });
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(selectAllCheckbox, spacer, countLabel);
        
        // TableView
        tableView = new TableView<>();
        tableView.setStyle("-fx-border-color: transparent; -fx-font-size: 12px;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(500);
        tableView.setMinHeight(400);
        
        // Colonne case à cocher
        TableColumn<Commande, Boolean> checkCol = new TableColumn<>("");
        checkCol.setPrefWidth(35);
        checkCol.setMinWidth(35);
        checkCol.setMaxWidth(35);
        checkCol.setResizable(false);
        checkCol.setCellFactory(col -> new TableCell<Commande, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(selectedIds.contains(cmd.getId()));
                    checkBox.setOnAction(e -> {
                        if (checkBox.isSelected()) {
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
        
        // Colonne N° Commande
        TableColumn<Commande, Integer> idCol = new TableColumn<>("N° CMD");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(70);
        idCol.setMinWidth(70);
        idCol.setCellFactory(col -> new TableCell<Commande, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText("#" + item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #E66239; -fx-font-size: 12px;");
                    setAlignment(Pos.CENTER);
                }
            }
        });
        
        // Colonne Client
        TableColumn<Commande, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientCol.setPrefWidth(160);
        clientCol.setMinWidth(130);
        clientCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3748; -fx-font-size: 12px;");
                }
            }
        });
        
        // Colonne Date
        TableColumn<Commande, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        dateCol.setPrefWidth(95);
        dateCol.setMinWidth(95);
        dateCol.setCellFactory(col -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            return new TableCell<Commande, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else {
                        setText(item.format(formatter));
                        setStyle("-fx-font-size: 12px;");
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });
        
        // Colonne Produits
        TableColumn<Commande, String> produitsCol = new TableColumn<>("Produits");
        produitsCol.setCellValueFactory(new PropertyValueFactory<>("produits"));
        produitsCol.setPrefWidth(180);
        produitsCol.setMinWidth(130);
        produitsCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] produits = item.split(",");
                    if (produits.length <= 2) {
                        setText(item);
                        setStyle("-fx-font-size: 11px;");
                        setGraphic(null);
                    } else {
                        setText(produits[0].trim() + ", " + produits[1].trim() + "...");
                        setStyle("-fx-font-size: 11px;");
                        Tooltip tip = new Tooltip(item);
                        tip.setStyle("-fx-font-size: 11px;");
                        setTooltip(tip);
                        setGraphic(null);
                    }
                }
            }
        });
        
        // Colonne Total
        TableColumn<Commande, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalTtc"));
        totalCol.setPrefWidth(100);
        totalCol.setMinWidth(90);
        totalCol.setCellFactory(col -> new TableCell<Commande, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(String.format("%,d F", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #059669; -fx-font-size: 12px;");
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });
        
        // ✅ Colonne Statut - AVEC SÉLECTEUR DE STATUT RESTAURÉ
        TableColumn<Commande, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statusCol.setPrefWidth(195);
        statusCol.setMinWidth(195);
        statusCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    
                    HBox container = new HBox(8);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Badge du statut
                    Label badge = new Label();
                    if ("en_attente".equals(item)) {
                        badge.setText("⏳ En attente");
                        badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-padding: 3px 10px; -fx-background-radius: 15px; -fx-font-size: 10px; -fx-font-weight: bold;");
                    } else if ("livree".equals(item)) {
                        badge.setText("✅ Livrée");
                        badge.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-padding: 3px 10px; -fx-background-radius: 15px; -fx-font-size: 10px; -fx-font-weight: bold;");
                    } else {
                        badge.setText("❌ Annulée");
                        badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 3px 10px; -fx-background-radius: 15px; -fx-font-size: 10px; -fx-font-weight: bold;");
                    }
                    
                    // ✅ Sélecteur de statut
                    ComboBox<String> statusCombo = new ComboBox<>();
                    statusCombo.getItems().addAll("en_attente", "livree", "annulee");
                    statusCombo.setValue(item);
                    statusCombo.setPrefWidth(90);
                    statusCombo.setStyle("-fx-font-size: 10px; -fx-background-radius: 10px;");
                    statusCombo.setOnAction(e -> {
                        String newStatus = statusCombo.getValue();
                        if (newStatus != null && !newStatus.equals(cmd.getStatut())) {
                            updateCommandeStatus(cmd.getId(), newStatus);
                        }
                    });
                    
                    container.getChildren().addAll(badge, statusCombo);
                    
                    // ✅ Lien "📄 Facture" qui apparaît si le statut est "livree"
                    if ("livree".equals(item)) {
                        Button factureLink = new Button("📄 Facture");
                        factureLink.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-padding: 3px 10px; -fx-background-radius: 15px; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
                        factureLink.setOnMouseEntered(e -> factureLink.setStyle("-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-padding: 3px 10px; -fx-background-radius: 15px; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;"));
                        factureLink.setOnMouseExited(e -> factureLink.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-padding: 3px 10px; -fx-background-radius: 15px; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;"));
                        factureLink.setOnAction(e -> ouvrirFormulaireFacture(cmd));
                        Tooltip.install(factureLink, new Tooltip("Ouvrir la facture pour impression"));
                        container.getChildren().add(factureLink);
                    }
                    
                    setGraphic(container);
                }
            }
        });
        
        // ✅ Colonne Actions - ICÔNES UNIQUEMENT
        TableColumn<Commande, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(110);
        actionsCol.setMinWidth(110);
        actionsCol.setResizable(false);
        actionsCol.setCellFactory(col -> new TableCell<Commande, Void>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏");
            private final Button deleteBtn = new Button("🗑");
            private final HBox buttons = new HBox(6, viewBtn, editBtn, deleteBtn);
            {
                buttons.setAlignment(Pos.CENTER);
                
                // Style des boutons icônes
                String baseStyle = "-fx-min-width: 32px; -fx-min-height: 32px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;";
                
                viewBtn.setStyle("-fx-background-color: #3b82f6;" + baseStyle);
                viewBtn.setOnMouseEntered(e -> viewBtn.setStyle("-fx-background-color: #2563eb;" + baseStyle));
                viewBtn.setOnMouseExited(e -> viewBtn.setStyle("-fx-background-color: #3b82f6;" + baseStyle));
                Tooltip.install(viewBtn, new Tooltip("Voir le détail"));
                
                editBtn.setStyle("-fx-background-color: #f59e0b;" + baseStyle);
                editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #d97706;" + baseStyle));
                editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #f59e0b;" + baseStyle));
                Tooltip.install(editBtn, new Tooltip("Modifier"));
                
                deleteBtn.setStyle("-fx-background-color: #ef4444;" + baseStyle);
                deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #dc2626;" + baseStyle));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #ef4444;" + baseStyle));
                Tooltip.install(deleteBtn, new Tooltip("Supprimer"));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    int commandeId = cmd.getId();
                    
                    viewBtn.setOnAction(e -> {
                        System.out.println("👁 Voir commande #" + commandeId);
                        Map<String, String> params = new HashMap<>();
                        params.put("id", String.valueOf(commandeId));
                        navigationManager.navigateToWithParams("commandeDetails", params);
                    });
                    
                    editBtn.setOnAction(e -> {
                        if ("livree".equals(cmd.getStatut())) {
                            AlertUtils.showWarningMessage("⚠️ Impossible de modifier une commande déjà livrée");
                        } else {
                            System.out.println("✏ Modifier commande #" + commandeId);
                            Map<String, String> params = new HashMap<>();
                            params.put("id", String.valueOf(commandeId));
                            navigationManager.navigateToWithParams("commandeEdit", params);
                        }
                    });
                    
                    deleteBtn.setOnAction(e -> {
                        if (AlertUtils.confirmDelete("🗑 Supprimer la commande #" + cmd.getId() + " ?\n\nCette action est irréversible.")) {
                            new Thread(() -> {
                                try {
                                    commandeService.deleteCommande(cmd.getId());
                                    Platform.runLater(() -> {
                                        commandeList.remove(cmd);
                                        updateStats();
                                        updateTablePage();
                                        AlertUtils.showSuccessMessage("✅ Commande #" + cmd.getId() + " supprimée avec succès");
                                    });
                                } catch (Exception ex) {
                                    Platform.runLater(() -> AlertUtils.showErrorMessage("❌ Erreur lors de la suppression: " + ex.getMessage()));
                                }
                            }).start();
                        }
                    });
                    
                    setGraphic(buttons);
                }
            }
        });
        
        tableView.getColumns().addAll(checkCol, idCol, clientCol, dateCol, produitsCol, totalCol, statusCol, actionsCol);
        tableView.setItems(filteredList);
        
        Label placeholder = new Label("📭 Aucune commande trouvée");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #a0aec0;");
        tableView.setPlaceholder(placeholder);
        
        card.getChildren().addAll(header, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        return card;
    }
    
    // ✅ Nouvelle méthode : Ouvrir le formulaire de facture
    private void ouvrirFormulaireFacture(Commande commande) {
        System.out.println("📄 Ouverture facture pour commande #" + commande.getId());
        
        // Créer une fenêtre (Stage) pour la facture
        javafx.stage.Stage factureStage = new javafx.stage.Stage();
        factureStage.setTitle("Facture - Commande #" + commande.getId());
        factureStage.setMinWidth(600);
        factureStage.setMinHeight(700);
        
        VBox factureContent = new VBox(20);
        factureContent.setPadding(new Insets(25));
        factureContent.setStyle("-fx-background-color: white;");
        
        // En-tête
        Label titleLabel = new Label("📄 FACTURE");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        Label numeroLabel = new Label("N° FACT-" + commande.getId() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        numeroLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        // Séparateur
        Separator sep1 = new Separator();
        
        // Informations client
        VBox clientBox = new VBox(8);
        clientBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-padding: 15px;");
        Label clientTitle = new Label("👤 Client");
        clientTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label clientName = new Label("Nom: " + (commande.getClientName() != null ? commande.getClientName() : "N/A"));
        Label clientTel = new Label("Tél: " + (commande.getClientTel() != null ? commande.getClientTel() : "N/A"));
        clientBox.getChildren().addAll(clientTitle, clientName, clientTel);
        
        // Informations commande
        VBox cmdBox = new VBox(8);
        cmdBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-padding: 15px;");
        Label cmdTitle = new Label("📋 Commande");
        cmdTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label cmdDate = new Label("Date: " + (commande.getDateCommande() != null ? commande.getDateCommande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        Label cmdStatus = new Label("Statut: ✅ Livrée");
        Label cmdTotal = new Label("Total: " + String.format("%,d FCFA", commande.getTotalTtc()));
        cmdTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #E66239;");
        cmdBox.getChildren().addAll(cmdTitle, cmdDate, cmdStatus, cmdTotal);
        
        // Produits
        Label produitsTitle = new Label("📦 Produits");
        produitsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        VBox produitsBox = new VBox(5);
        String produits = commande.getProduits();
        if (produits != null && !produits.isEmpty()) {
            for (String p : produits.split(",")) {
                Label pLabel = new Label("• " + p.trim());
                pLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4a5568;");
                produitsBox.getChildren().add(pLabel);
            }
        }
        
        Separator sep2 = new Separator();
        
        // ✅ Bouton Imprimer
        Button imprimerBtn = new Button("🖨️ Imprimer la facture");
        imprimerBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 12px 24px; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        imprimerBtn.setMaxWidth(Double.MAX_VALUE);
        imprimerBtn.setOnMouseEntered(e -> imprimerBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-padding: 12px 24px; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;"));
        imprimerBtn.setOnMouseExited(e -> imprimerBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 12px 24px; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;"));
        imprimerBtn.setOnAction(e -> {
            // Logique d'impression
            AlertUtils.showInfoMessage("🖨️ Impression lancée pour la facture #" + commande.getId());
            System.out.println("🖨️ Impression facture #" + commande.getId());
        });
        
        // Bouton Fermer
        Button fermerBtn = new Button("✕ Fermer");
        fermerBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand;");
        fermerBtn.setOnAction(e -> factureStage.close());
        
        HBox actionsBox = new HBox(10, imprimerBtn, fermerBtn);
        actionsBox.setAlignment(Pos.CENTER);
        
        factureContent.getChildren().addAll(titleLabel, numeroLabel, sep1, clientBox, cmdBox, produitsTitle, produitsBox, sep2, actionsBox);
        
        ScrollPane scrollPane = new ScrollPane(factureContent);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, 550, 650);
        factureStage.setScene(scene);
        factureStage.show();
    }
    
    private void updateCommandeStatus(int id, String newStatus) {
        new Thread(() -> {
            try {
                commandeService.updateStatut(id, newStatus);
                Platform.runLater(() -> {
                    for (Commande cmd : commandeList) {
                        if (cmd.getId() == id) {
                            cmd.setStatut(newStatus);
                            break;
                        }
                    }
                    tableView.refresh();
                    updateStats();
                    AlertUtils.showSuccessMessage("✅ Statut mis à jour avec succès");
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("❌ Erreur lors de la mise à jour: " + e.getMessage()));
            }
        }).start();
    }
    
    private HBox createPagination() {
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(15, 0, 0, 0));
        return pagination;
    }
    
    private HBox createBulkBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 40px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);");
        bar.setVisible(false);
        bar.setManaged(false);
        
        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        
        selectedCountLabel = new Label("0");
        selectedCountLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label textLabel = new Label("sélectionnée(s)");
        textLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
        
        bulkStatusCombo = new ComboBox<>();
        bulkStatusCombo.getItems().addAll("Changer statut...", "⏳ En attente", "✅ Livrée", "❌ Annulée");
        bulkStatusCombo.setValue("Changer statut...");
        bulkStatusCombo.setStyle("-fx-background-radius: 20px; -fx-font-size: 12px; -fx-min-width: 160px;");
        bulkStatusCombo.setOnAction(e -> {
            String val = bulkStatusCombo.getValue();
            String status = null;
            if (val.contains("En attente")) status = "en_attente";
            else if (val.contains("Livrée")) status = "livree";
            else if (val.contains("Annulée")) status = "annulee";
            
            if (status != null && !selectedIds.isEmpty()) {
                final String finalStatus = status;
                new Thread(() -> {
                    try {
                        for (int id : selectedIds) {
                            commandeService.updateStatut(id, finalStatus);
                        }
                        Platform.runLater(() -> {
                            for (Commande cmd : commandeList) {
                                if (selectedIds.contains(cmd.getId())) {
                                    cmd.setStatut(finalStatus);
                                }
                            }
                            tableView.refresh();
                            updateStats();
                            AlertUtils.showSuccessMessage("✅ " + selectedIds.size() + " commande(s) mise(s) à jour");
                            bulkStatusCombo.setValue("Changer statut...");
                            clearSelection();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> AlertUtils.showErrorMessage("❌ Erreur: " + ex.getMessage()));
                    }
                }).start();
            }
        });
        
        Button deleteBtn = new Button("🗑 Supprimer la sélection");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8px 16px; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            if (AlertUtils.confirmDelete("🗑 Supprimer " + selectedIds.size() + " commande(s) ?\n\nCette action est irréversible.")) {
                new Thread(() -> {
                    try {
                        for (int id : selectedIds) {
                            commandeService.deleteCommande(id);
                        }
                        Platform.runLater(() -> {
                            commandeList.removeIf(cmd -> selectedIds.contains(cmd.getId()));
                            updateStats();
                            updateTablePage();
                            AlertUtils.showSuccessMessage("✅ " + selectedIds.size() + " commande(s) supprimée(s)");
                            clearSelection();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> AlertUtils.showErrorMessage("❌ Erreur: " + ex.getMessage()));
                    }
                }).start();
            }
        });
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px;"));
        closeBtn.setOnAction(e -> clearSelection());
        
        bar.getChildren().addAll(iconLabel, selectedCountLabel, textLabel, bulkStatusCombo, deleteBtn, closeBtn);
        return bar;
    }
    
    private void clearSelection() {
        selectedIds.clear();
        selectAllCheckbox.setSelected(false);
        updateBulkBar();
        tableView.refresh();
    }
    
    private void loadData() {
        System.out.println("📊 Chargement des commandes...");
        new Thread(() -> {
            try {
                List<Commande> commandes = commandeService.getAllCommandes();
                Map<String, Integer> stats = commandeService.getStats();
                
                Platform.runLater(() -> {
                    commandeList.setAll(commandes);
                    tableView.setItems(filteredList);
                    updateStatsFromMap(stats);
                    updateTablePage();
                    System.out.println("✅ " + commandes.size() + " commandes chargées");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("❌ Erreur de chargement: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    public void refreshData() {
        System.out.println("🔄 Rafraîchissement des commandes...");
        new Thread(() -> {
            try {
                List<Commande> commandes = commandeService.getAllCommandes();
                Map<String, Integer> stats = commandeService.getStats();
                
                Platform.runLater(() -> {
                    commandeList.setAll(commandes);
                    tableView.setItems(filteredList);
                    updateStatsFromMap(stats);
                    updateTablePage();
                    System.out.println("✅ Rafraîchissement terminé: " + commandes.size() + " commandes");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("❌ Erreur de rafraîchissement: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void updateStatsFromMap(Map<String, Integer> stats) {
        totalCountLabel.setText(String.valueOf(stats.getOrDefault("total", 0)));
        pendingCountLabel.setText(String.valueOf(stats.getOrDefault("attente", 0)));
        deliveredCountLabel.setText(String.valueOf(stats.getOrDefault("livree", 0)));
        cancelledCountLabel.setText(String.valueOf(stats.getOrDefault("annulee", 0)));
    }
    
    private void filterData() {
        if (searchField == null || dateFilter == null || filteredList == null) return;
        
        String search = searchField.getText().toLowerCase().trim();
        LocalDate date = dateFilter.getValue();
        
        filteredList.setPredicate(cmd -> {
            if (cmd == null) return false;
            
            if (!"all".equals(currentFilter) && !cmd.getStatut().equals(currentFilter)) return false;
            
            if (!search.isEmpty()) {
                String idStr = String.valueOf(cmd.getId());
                String clientName = cmd.getClientName() != null ? cmd.getClientName().toLowerCase() : "";
                if (!clientName.contains(search) && !idStr.contains(search)) return false;
            }
            
            if (date != null) {
                LocalDate cmdDate = cmd.getDateCommande();
                if (cmdDate == null || !cmdDate.equals(date)) return false;
            }
            
            return true;
        });
        
        updateStats();
        updateTablePage();
    }
    
    private void updateStats() {
        if (filteredList == null) return;
        
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
        if (paginationBox == null || filteredList == null) return;
        
        paginationBox.getChildren().clear();
        int totalPages = (int) Math.ceil(filteredList.size() / (double) rowsPerPage);
        
        if (filteredList.isEmpty()) {
            tableView.setItems(FXCollections.observableArrayList());
            return;
        }
        
        if (totalPages <= 1) {
            tableView.setItems(filteredList);
            return;
        }
        
        currentPage = Math.max(1, Math.min(currentPage, totalPages));
        
        Button prevBtn = new Button("◀ Précédent");
        prevBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 100px; -fx-min-height: 36px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand; -fx-font-size: 12px;");
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTablePage();
            }
        });
        if (currentPage == 1) prevBtn.setDisable(true);
        
        HBox pagesBox = new HBox(5);
        pagesBox.setAlignment(Pos.CENTER);
        
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        
        if (startPage > 1) {
            Label dots = new Label("...");
            dots.setStyle("-fx-text-fill: #a0aec0;");
            pagesBox.getChildren().add(dots);
        }
        
        for (int i = startPage; i <= endPage; i++) {
            int pageNum = i;
            Button pageBtn = new Button(String.valueOf(i));
            pageBtn.setMinWidth(36);
            pageBtn.setMinHeight(36);
            if (currentPage == i) {
                pageBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 36px; -fx-min-height: 36px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand;");
            } else {
                pageBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 36px; -fx-min-height: 36px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand;");
            }
            pageBtn.setOnAction(e -> {
                currentPage = pageNum;
                updateTablePage();
            });
            pagesBox.getChildren().add(pageBtn);
        }
        
        if (endPage < totalPages) {
            Label dots = new Label("...");
            dots.setStyle("-fx-text-fill: #a0aec0;");
            pagesBox.getChildren().add(dots);
        }
        
        Button nextBtn = new Button("Suivant ▶");
        nextBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 100px; -fx-min-height: 36px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand; -fx-font-size: 12px;");
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTablePage();
            }
        });
        if (currentPage == totalPages) nextBtn.setDisable(true);
        
        paginationBox.getChildren().addAll(prevBtn, pagesBox, nextBtn);
        
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredList.size());
        List<Commande> subList = new ArrayList<>(filteredList.subList(start, end));
        tableView.setItems(FXCollections.observableArrayList(subList));
    }
    
    private void updateBulkBar() {
        boolean show = selectedIds.size() >= 1;
        bulkBar.setVisible(show);
        bulkBar.setManaged(show);
        if (show) {
            selectedCountLabel.setText(String.valueOf(selectedIds.size()));
        }
    }
    
    private void exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("N° Commande;Client;Date;Produits;Montant;Statut\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Commande cmd : commandeList) {
            csv.append(cmd.getId()).append(";");
            csv.append(cmd.getClientName() != null ? cmd.getClientName() : "").append(";");
            csv.append(cmd.getDateCommande() != null ? cmd.getDateCommande().format(formatter) : "").append(";");
            csv.append("\"").append(cmd.getProduits() != null ? cmd.getProduits() : "").append("\";");
            csv.append(cmd.getTotalTtc()).append(";");
            csv.append(cmd.getStatut()).append("\n");
        }
        
        AlertUtils.showInfoMessage("📥 Export CSV - " + commandeList.size() + " commandes exportées\n" + csv.toString());
        System.out.println("📥 Export CSV:\n" + csv.toString());
    }
}