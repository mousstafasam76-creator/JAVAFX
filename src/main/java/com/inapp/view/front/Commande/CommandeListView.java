package com.inapp.view.front.commande;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
        this.commandeService = new CommandeService();
        this.commandeList = FXCollections.observableArrayList();
        setupUI();
        loadData();
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
        Label title = new Label("📋 Commandes");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label subtitle = new Label("Gérez vos commandes et suivez leur statut");
        subtitle.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox actionsBox = new HBox(10);
        
        Button exportBtn = new Button("📥 Exporter");
        exportBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; -fx-padding: 8px 16px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 500;");
        exportBtn.setOnAction(e -> exportToCSV());
        
        Button addBtn = new Button("➕ Nouvelle commande");
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
        
        VBox card1 = createStatCard("📦", "Total commandes", totalCountLabel, "#667eea", "all");
        VBox card2 = createStatCard("⏳", "En attente", pendingCountLabel, "#f59e0b", "en_attente");
        VBox card3 = createStatCard("🚚", "Livrées", deliveredCountLabel, "#10b981", "livree");
        VBox card4 = createStatCard("❌", "Annulées", cancelledCountLabel, "#ef4444", "annulee");
        
        statsBox.getChildren().addAll(card1, card2, card3, card4);
        for (int i = 0; i < 4; i++) {
            HBox.setHgrow(statsBox.getChildren().get(i), Priority.ALWAYS);
        }
        
        return statsBox;
    }
    
    private VBox createStatCard(String icon, String title, Label valueLabel, String color, String filterValue) {
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
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        header.getChildren().addAll(iconLabel, spacer, titleLabel);
        
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
        Label searchIcon = new Label("🔍");
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
        
        // Colonne checkbox
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
        
        // N° Commande
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
        
        // Client
        TableColumn<Commande, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientCol.setPrefWidth(200);
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
        
        // Date
        TableColumn<Commande, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        dateCol.setPrefWidth(120);
        dateCol.setCellFactory(col -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return new TableCell<Commande, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(item.format(formatter));
                }
            };
        });
        
        // Produits
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
        
        // Total
        TableColumn<Commande, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalTtc"));
        totalCol.setPrefWidth(120);
        totalCol.setCellFactory(col -> new TableCell<Commande, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else {
                    setText(String.format("%,d FCFA", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");
                }
            }
        });
        
        // Statut
        TableColumn<Commande, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statusCol.setPrefWidth(150);
        statusCol.setCellFactory(col -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    Label badge = new Label();
                    if ("en_attente".equals(item)) {
                        badge.setText("⏳ En attente");
                        badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 11px;");
                    } else if ("livree".equals(item)) {
                        badge.setText("✅ Livrée");
                        badge.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 11px;");
                    } else {
                        badge.setText("❌ Annulée");
                        badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 11px;");
                    }
                    
                    ComboBox<String> statusCombo = new ComboBox<>();
                    statusCombo.getItems().addAll("en_attente", "livree", "annulee");
                    statusCombo.setValue(item);
                    statusCombo.setStyle("-fx-font-size: 11px; -fx-min-width: 100px;");
                    statusCombo.setOnAction(e -> {
                        String newStatus = statusCombo.getValue();
                        if (newStatus != null && !newStatus.equals(item)) {
                            Commande cmd = getTableView().getItems().get(getIndex());
                            updateCommandeStatus(cmd.getId(), newStatus);
                        }
                    });
                    
                    container.getChildren().addAll(badge, statusCombo);
                    setGraphic(container);
                }
            }
        });
        
        // Actions
        TableColumn<Commande, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(130);
        actionsCol.setCellFactory(col -> new TableCell<Commande, Void>() {
            private final Button viewBtn = new Button("👁️");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
            {
                buttons.setAlignment(Pos.CENTER);
                viewBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 6px; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 6px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-min-width: 34px; -fx-min-height: 34px; -fx-background-radius: 6px; -fx-cursor: hand;");
                Tooltip.install(viewBtn, new Tooltip("Voir le détail"));
                Tooltip.install(editBtn, new Tooltip("Modifier"));
                Tooltip.install(deleteBtn, new Tooltip("Supprimer"));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    
                    viewBtn.setOnAction(e -> {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", String.valueOf(cmd.getId()));
                        navigationManager.navigateToWithParams("commandeDetail", params);
                    });
                    
                    editBtn.setOnAction(e -> {
                        if ("livree".equals(cmd.getStatut())) {
                            AlertUtils.showWarningMessage("Impossible de modifier une commande livrée");
                        } else {
                            Map<String, String> params = new HashMap<>();
                            params.put("id", String.valueOf(cmd.getId()));
                            navigationManager.navigateToWithParams("commandeEdit", params);
                        }
                    });
                    
                    deleteBtn.setOnAction(e -> {
                        if (AlertUtils.confirmDelete("Supprimer la commande #" + cmd.getId() + " ?")) {
                            new Thread(() -> {
                                try {
                                    commandeService.deleteCommande(cmd.getId());
                                    Platform.runLater(() -> {
                                        commandeList.remove(cmd);
                                        updateStats();
                                        updateTablePage();
                                        AlertUtils.showSuccessMessage("Commande supprimée");
                                    });
                                } catch (Exception ex) {
                                    Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de la suppression"));
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
        
        card.getChildren().addAll(header, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        return card;
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
                    AlertUtils.showSuccessMessage("Statut mis à jour");
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de la mise à jour"));
            }
        }).start();
    }
    
    private HBox createPagination() {
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(20, 0, 0, 0));
        return pagination;
    }
    
    private HBox createBulkBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 40px;");
        bar.setVisible(false);
        
        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        selectedCountLabel = new Label("0");
        selectedCountLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label textLabel = new Label("sélectionnée(s)");
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        
        bulkStatusCombo = new ComboBox<>();
        bulkStatusCombo.getItems().addAll("Changer statut", "en_attente", "livree", "annulee");
        bulkStatusCombo.setValue("Changer statut");
        bulkStatusCombo.setStyle("-fx-background-radius: 20px; -fx-font-size: 12px;");
        bulkStatusCombo.setOnAction(e -> {
            String val = bulkStatusCombo.getValue();
            if (!val.equals("Changer statut") && !selectedIds.isEmpty()) {
                new Thread(() -> {
                    try {
                        for (int id : selectedIds) {
                            commandeService.updateStatut(id, val);
                        }
                        Platform.runLater(() -> {
                            for (Commande cmd : commandeList) {
                                if (selectedIds.contains(cmd.getId())) {
                                    cmd.setStatut(val);
                                }
                            }
                            tableView.refresh();
                            updateStats();
                            AlertUtils.showSuccessMessage(selectedIds.size() + " commande(s) mise(s) à jour");
                            bulkStatusCombo.setValue("Changer statut");
                            clearSelection();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de la mise à jour"));
                    }
                }).start();
            }
        });
        
        Button deleteBtn = new Button("🗑️ Supprimer");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 6px 16px; -fx-background-radius: 20px; -fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            if (AlertUtils.confirmDelete("Supprimer " + selectedIds.size() + " commande(s) ?")) {
                new Thread(() -> {
                    try {
                        for (int id : selectedIds) {
                            commandeService.deleteCommande(id);
                        }
                        Platform.runLater(() -> {
                            for (int id : selectedIds) {
                                commandeList.removeIf(cmd -> cmd.getId() == id);
                            }
                            updateStats();
                            updateTablePage();
                            AlertUtils.showSuccessMessage(selectedIds.size() + " commande(s) supprimée(s)");
                            clearSelection();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Erreur lors de la suppression"));
                    }
                }).start();
            }
        });
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px; -fx-font-weight: bold;");
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
        new Thread(() -> {
            try {
                List<Commande> commandes = commandeService.getAllCommandes();
                Map<String, Integer> stats = commandeService.getStats();
                
                Platform.runLater(() -> {
                    commandeList.setAll(commandes);
                    tableView.setItems(filteredList);
                    updateStatsFromMap(stats);
                    updateTablePage();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Erreur de chargement des données: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Rafraîchit les données de la liste des commandes
     * Appelé après une modification dans Edit
     */
    public void refreshData() {
        System.out.println("🔄 Rafraîchissement des données de la liste");
        new Thread(() -> {
            try {
                List<Commande> commandes = commandeService.getAllCommandes();
                Map<String, Integer> stats = commandeService.getStats();
                
                Platform.runLater(() -> {
                    commandeList.setAll(commandes);
                    tableView.setItems(filteredList);
                    updateStatsFromMap(stats);
                    updateTablePage();
                    System.out.println("✅ Données rafraîchies avec succès");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Erreur de rafraîchissement: " + e.getMessage());
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
        // Ajout des vérifications null pour éviter l'erreur
        if (searchField == null || dateFilter == null || filteredList == null) {
            return;
        }
        
        String search = searchField.getText().toLowerCase();
        LocalDate date = dateFilter.getValue();
        
        filteredList.setPredicate(cmd -> {
            if (!"all".equals(currentFilter) && !cmd.getStatut().equals(currentFilter)) return false;
            if (!search.isEmpty() && !cmd.getClientName().toLowerCase().contains(search) && 
                !String.valueOf(cmd.getId()).contains(search)) return false;
            if (date != null && cmd.getDateCommande() != null && !cmd.getDateCommande().equals(date)) return false;
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
        
        if (totalPages <= 1) {
            tableView.setItems(filteredList);
            return;
        }
        
        currentPage = Math.max(1, Math.min(currentPage, totalPages));
        
        Button prevBtn = new Button("◀");
        prevBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand;");
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
                pageBtn.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-min-width: 36px; -fx-min-height: 36px; -fx-background-radius: 8px; -fx-font-weight: bold;");
            } else {
                pageBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 36px; -fx-min-height: 36px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
            }
            pageBtn.setOnAction(e -> {
                currentPage = pageNum;
                updateTablePage();
            });
            pagesBox.getChildren().add(pageBtn);
        }
        
        Button nextBtn = new Button("▶");
        nextBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4a5568; -fx-min-width: 38px; -fx-min-height: 38px; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-cursor: hand;");
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
    
    private void exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("N° Commande;Client;Date;Produits;Montant;Statut\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Commande cmd : commandeList) {
            csv.append(cmd.getId()).append(";");
            csv.append(cmd.getClientName()).append(";");
            csv.append(cmd.getDateCommande() != null ? cmd.getDateCommande().format(formatter) : "").append(";");
            csv.append("\"").append(cmd.getProduits() != null ? cmd.getProduits() : "").append("\";");
            csv.append(cmd.getTotalTtc()).append(";");
            csv.append(cmd.getStatut()).append("\n");
        }
        
        AlertUtils.showInfoMessage("Export CSV lancé - " + commandeList.size() + " commandes exportées");
    }
}