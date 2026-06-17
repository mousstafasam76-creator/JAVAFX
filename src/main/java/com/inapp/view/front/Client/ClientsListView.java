package com.inapp.view.front.Client;

import com.inapp.controller.front.ClientController;
import com.inapp.model.Client;
import com.inapp.utils.AlertUtils;
import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Footer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientsListView extends BorderPane {

    private static final String PRIMARY = "#E66239";
    private static final String PRIMARY_DARK = "#d5542e";
    private static final String BG = "#f5f7fa";
    private static final String BORDER = "#e2e8f0";

    private final NavigationManager navManager;
    private final ClientController controller;
    private TableView<Client> tableView;
    private Label totalClientsLabel, clientsActifsLabel, caLabel;
    private FilteredList<Client> filteredList;

    public ClientsListView(NavigationManager navManager) {
        this.navManager = navManager;
        this.controller = ClientController.getInstance();
        setupUI();
    }

    private void setupUI() {
        // SUPPRIMER le Sidebar ici (déjà dans MainApplication)
        setBottom(new Footer());

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: " + BG + ";");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("👥 Clients");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label subtitle = new Label("Gérez votre portefeuille clients");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Nouveau client");
        addBtn.setStyle(
            "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        );
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(
            "-fx-background-color: " + PRIMARY_DARK + "; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        ));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(
            "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        ));
        addBtn.setOnAction(e -> navManager.navigateTo("clientAdd"));

        header.getChildren().addAll(titleBox, spacer, addBtn);

        HBox statsRow = createStatsCards();
        HBox filterBar = createFilterBar();
        tableView = createTable();

        root.getChildren().addAll(header, statsRow, filterBar, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        refreshData();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG + ";");

        setCenter(scrollPane);

        refreshStats();
    }

    private HBox createStatsCards() {
        HBox row = new HBox(20);

        totalClientsLabel = new Label("0");
        clientsActifsLabel = new Label("0");
        caLabel = new Label("0");

        row.getChildren().addAll(
            createStatCard("Total clients", totalClientsLabel, PRIMARY),
            createStatCard("Clients actifs", clientsActifsLabel, "#10b981"),
            createStatCard("Chiffre d'affaires (FCFA)", caLabel, "#6366f1")
        );
        return row;
    }

    private VBox createStatCard(String label, Label valueLabel, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 16;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);

        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        card.getChildren().addAll(valueLabel, lbl);
        return card;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 16;"
        );

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(4, 14, 4, 14));
        searchBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 40;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 13px;");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher (nom, téléphone, email)...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);

        Button sortAsc = createOrderBtn("A → Z");
        Button sortDesc = createOrderBtn("Z → A");

        sortAsc.setOnAction(e -> {
            sortAsc.setStyle(activeOrderStyle());
            sortDesc.setStyle(orderBtnStyle());
            if (filteredList != null) {
                SortedList<Client> sorted = new SortedList<>(filteredList);
                sorted.setComparator((a, b) -> a.getPrenom().compareToIgnoreCase(b.getPrenom()));
                tableView.setItems(sorted);
            }
        });
        sortDesc.setOnAction(e -> {
            sortDesc.setStyle(activeOrderStyle());
            sortAsc.setStyle(orderBtnStyle());
            if (filteredList != null) {
                SortedList<Client> sorted = new SortedList<>(filteredList);
                sorted.setComparator((a, b) -> b.getPrenom().compareToIgnoreCase(a.getPrenom()));
                tableView.setItems(sorted);
            }
        });

        searchField.textProperty().addListener((obs, o, n) -> {
            if (filteredList != null) {
                String q = n.toLowerCase();
                filteredList.setPredicate(c -> {
                    if (q.isEmpty()) return true;
                    return c.getNomComplet().toLowerCase().contains(q)
                        || (c.getTel() != null && c.getTel().toLowerCase().contains(q))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(q));
                });
                refreshStats();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        bar.getChildren().addAll(searchBox, spacer, sortAsc, sortDesc);
        return bar;
    }

    private String orderBtnStyle() {
        return "-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10;" +
               "-fx-background-radius: 10; -fx-padding: 8 16; -fx-font-size: 12px; -fx-cursor: hand;";
    }
    private String activeOrderStyle() {
        return "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-border-color: " + PRIMARY + "; -fx-border-radius: 10;" +
               "-fx-background-radius: 10; -fx-padding: 8 16; -fx-font-size: 12px; -fx-cursor: hand;";
    }
    private Button createOrderBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(orderBtnStyle());
        return btn;
    }

    private TableView<Client> createTable() {
        TableView<Client> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: " + BORDER + "; -fx-border-radius: 16;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Aucun client trouvé"));

        TableColumn<Client, String> colNom = new TableColumn<>("Client");
        colNom.setCellValueFactory(c -> {
            Client cl = c.getValue();
            return new javafx.beans.property.SimpleStringProperty(cl.getNomComplet());
        });
        colNom.setPrefWidth(200);
        colNom.setCellFactory(col -> new TableCell<Client, String>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setGraphic(null); }
                else {
                    Label lbl = new Label(v);
                    lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    setGraphic(lbl);
                    setText(null);
                }
            }
        });

        TableColumn<Client, String> colTel = new TableColumn<>("Téléphone");
        colTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colTel.setPrefWidth(140);

        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(220);

        TableColumn<Client, String> colAdresse = new TableColumn<>("Adresse");
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colAdresse.setPrefWidth(200);

        TableColumn<Client, Integer> colCmds = new TableColumn<>("Commandes");
        colCmds.setCellValueFactory(new PropertyValueFactory<>("nbCommandes"));
        colCmds.setPrefWidth(100);
        colCmds.setCellFactory(col -> new TableCell<Client, Integer>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); } else {
                    Label badge = new Label(String.valueOf(v));
                    badge.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #1e293b;" +
                        "-fx-padding: 3 10; -fx-background-radius: 20; -fx-font-size: 12px;");
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Client, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(130);
        colActions.setCellFactory(col -> new TableCell<Client, Void>() {
            private final Button viewBtn = createActionBtn("👁", PRIMARY, "Voir");
            private final Button editBtn = createActionBtn("✏", "#f59e0b", "Modifier");
            private final Button delBtn  = createActionBtn("🗑", "#ef4444", "Supprimer");
            private final HBox box = new HBox(6, viewBtn, editBtn, delBtn);
            { box.setAlignment(Pos.CENTER); }

            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Client c = getTableView().getItems().get(getIndex());
                
                // CORRECTION : Utiliser navigateToWithParams au lieu de navigateTo avec URL
                viewBtn.setOnAction(e -> {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(c.getId()));
                    navManager.navigateToWithParams("clientView", params);
                });
                
                editBtn.setOnAction(e -> {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(c.getId()));
                    navManager.navigateToWithParams("clientEdit", params);
                });
                
                delBtn.setOnAction(e -> {
                    if (AlertUtils.confirmDelete("Supprimer " + c.getNomComplet() + " ?")) {
                        controller.deleteClient(c);
                        refreshData();
                        AlertUtils.showSuccessMessage("Client supprimé !");
                    }
                });
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colNom, colTel, colEmail, colAdresse, colCmds, colActions);

        return table;
    }

    private Button createActionBtn(String icon, String color, String tooltip) {
        Button btn = new Button(icon);
        String base = "-fx-background-color: white; -fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5 9; -fx-font-size: 13px; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-border-color: " + color + ";" +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5 9; -fx-font-size: 13px; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        Tooltip.install(btn, new Tooltip(tooltip));
        return btn;
    }

    private void refreshData() {
        ObservableList<Client> data = controller.getClients();
        filteredList = new FilteredList<>(data, c -> true);
        tableView.setItems(filteredList);
        refreshStats();
    }

    private void refreshStats() {
        ObservableList<Client> visible = filteredList != null
            ? FXCollections.observableArrayList(filteredList)
            : controller.getClients();

        totalClientsLabel.setText(String.valueOf(visible.size()));
        long actifs = visible.stream().filter(c -> c.getNbCommandes() > 0).count();
        clientsActifsLabel.setText(String.valueOf(actifs));
        double ca = visible.stream().mapToDouble(Client::getTotalAchats).sum();
        caLabel.setText(NumberFormat.getNumberInstance(Locale.FRENCH).format((long) ca));
    }
}