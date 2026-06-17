package com.inapp.view.front.facture;

import com.inapp.controller.front.FactureController;
import com.inapp.model.Facture;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;

public class FactureList {

    private FactureController controller;
    private int currentPage = 1;
    private int rowsPerPage = 5;
    private String currentFilter = "all";
    private Runnable navigateToDetail;
    private Runnable navigateToForm;

    private TableView<Facture> table;
    private TextField searchField;
    private DatePicker datePicker;
    private Label pageLabel;
    private HBox bulkBar;
    private Label selectedCountLabel;

    public FactureList(FactureController controller) { this.controller = controller; }
    public void setNavigateToDetail(Runnable r) { this.navigateToDetail = r; }
    public void setNavigateToForm(Runnable r) { this.navigateToForm = r; }

    public VBox createView() {
        table = new TableView<>();
        searchField = new TextField();
        datePicker = new DatePicker();
        pageLabel = new Label("Page 1");
        bulkBar = new HBox(10);
        selectedCountLabel = new Label("0");

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f8fafc;");

        // ========== EN-TÊTE ==========
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label titre = new Label("📄 Factures");
        titre.setFont(Font.font("System", FontWeight.BOLD, 26));
        Label subtitle = new Label("Gérez vos factures et suivez les paiements");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(titre, subtitle);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button createBtn = new Button("➕ Nouvelle facture");
        createBtn.setStyle(
            "-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-background-radius: 12px; -fx-padding: 12 24; -fx-cursor: hand; -fx-font-size: 14px;"
        );
        createBtn.setOnAction(e -> { if (navigateToForm != null) navigateToForm.run(); });

        header.getChildren().addAll(titleBox, createBtn);

        // ========== CARTES STATISTIQUES ==========
        HBox statsGrid = new HBox(16);
        statsGrid.getChildren().addAll(
            createStatCard("📄", "Total factures", String.valueOf(controller.getTotal()), "#E66239", "all"),
            createStatCard("✅", "Payées", String.valueOf(controller.getPayees()), "#10b981", "paid"),
            createStatCard("❌", "Impayées", String.valueOf(controller.getImpayees()), "#ef4444", "unpaid"),
            createStatCard("💰", "À encaisser", formatMoney(controller.getTotalImpaye()), "#f59e0b", "amount")
        );

        // ========== BARRE DE FILTRES ==========
        HBox filterBar = new HBox(12);
        filterBar.setPadding(new Insets(16, 20, 16, 20));
        filterBar.setStyle(
            "-fx-background-color: white; -fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 20px; -fx-background-radius: 20px;"
        );
        filterBar.setAlignment(Pos.CENTER_LEFT);

        ToggleButton allBtn = createFilterTab("Toutes");
        ToggleButton paidBtn = createFilterTab("Payées");
        ToggleButton unpaidBtn = createFilterTab("Impayées");
        ToggleGroup filterGroup = new ToggleGroup();
        allBtn.setToggleGroup(filterGroup);
        paidBtn.setToggleGroup(filterGroup);
        unpaidBtn.setToggleGroup(filterGroup);
        allBtn.setSelected(true);

        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == allBtn) currentFilter = "all";
            else if (newVal == paidBtn) currentFilter = "paid";
            else currentFilter = "unpaid";
            applyFiltersAndRefresh();
        });

        searchField.setPromptText("🔍 N° facture, client...");
        searchField.setStyle(
            "-fx-background-color: #f1f5f9; -fx-background-radius: 40px;" +
            "-fx-padding: 10 16; -fx-font-size: 13px;"
        );
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndRefresh());

        datePicker.setPromptText("📅 Date");
        datePicker.setPrefWidth(150);
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndRefresh());

        filterBar.getChildren().addAll(allBtn, paidBtn, unpaidBtn, searchField, datePicker);

        // ========== TABLEAU ==========
        table.setItems(controller.getFilteredFactures());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setStyle(
            "-fx-background-color: white; -fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 20px; -fx-background-radius: 20px;"
        );

        TableColumn<Facture, String> numCol = new TableColumn<>("N° Facture");
        numCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomf()));
        numCol.setPrefWidth(130);

        TableColumn<Facture, String> clientCol = new TableColumn<>("Client(s)");
        clientCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClients()));
        clientCol.setPrefWidth(180);

        TableColumn<Facture, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getDatef().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        dateCol.setPrefWidth(100);

        TableColumn<Facture, Number> nbCmdCol = new TableColumn<>("Nb Cdes");
        nbCmdCol.setCellValueFactory(new PropertyValueFactory<>("nbCommandes"));
        nbCmdCol.setPrefWidth(70);
        nbCmdCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Facture, Number> montantCol = new TableColumn<>("Montant");
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        montantCol.setPrefWidth(130);
        montantCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(formatMoney(item.doubleValue())); setStyle("-fx-font-weight: bold;"); }
            }
        });

        TableColumn<Facture, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isPayee() ? "Payée" : "Impayée"));
        statutCol.setPrefWidth(110);
        statutCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else {
                    Label badge = new Label((item.equals("Payée") ? "✅ " : "❌ ") + item);
                    badge.setStyle(
                        "-fx-background-color: " + (item.equals("Payée") ? "#10b981" : "#ef4444") + ";" +
                        "-fx-text-fill: white; -fx-padding: 4 14; -fx-background-radius: 30px; -fx-font-size: 12px;"
                    );
                    setGraphic(badge);
                }
            }
        });

        // Colonne Actions
        TableColumn<Facture, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(170);
        actionsCol.setMinWidth(170);
        actionsCol.setMaxWidth(170);
        actionsCol.setResizable(false);
        actionsCol.setSortable(false);

        actionsCol.setCellFactory(tc -> new TableCell<>() {
            private final Button btnVoir = createActionBtn("👁", "#E66239");
            private final Button btnImprimer = createActionBtn("🖨", "#10b981");
            private final Button btnPayer = createActionBtn("💲", "#10b981");
            private final Button btnSupprimer = createActionBtn("🗑", "#ef4444");
            private final HBox box = new HBox(4, btnVoir, btnImprimer, btnPayer, btnSupprimer);
            {
                box.setAlignment(Pos.CENTER);
                btnVoir.setOnAction(e -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    FactureDetail.setFactureId(f.getId());
                    if (navigateToDetail != null) navigateToDetail.run();
                });
                btnImprimer.setOnAction(e -> FacturePrint.show(getTableView().getItems().get(getIndex()).getId()));
                btnPayer.setOnAction(e -> {
                    controller.markAsPaid(getTableView().getItems().get(getIndex()));
                    refreshTable();
                });
                btnSupprimer.setOnAction(e -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + f.getNomf() + " ?", ButtonType.YES, ButtonType.NO)
                        .showAndWait().ifPresent(r -> {
                            if (r == ButtonType.YES) { controller.deleteFacture(f); refreshTable(); }
                        });
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(numCol, clientCol, dateCol, nbCmdCol, montantCol, statutCol, actionsCol);

        // ========== BARRE GROUPÉE ==========
        bulkBar.setPadding(new Insets(14, 28, 14, 28));
        bulkBar.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 60px;");
        bulkBar.setAlignment(Pos.CENTER);
        bulkBar.setVisible(false);
        selectedCountLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button bulkPayBtn = new Button("✅ Marquer payées");
        bulkPayBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 30px; -fx-padding: 8 20; -fx-cursor: hand;");
        bulkPayBtn.setOnAction(e -> { controller.markMultipleAsPaid(getSelectedFactures()); refreshTable(); });

        Button bulkDeleteBtn = new Button("🗑 Supprimer");
        bulkDeleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 30px; -fx-padding: 8 20; -fx-cursor: hand;");
        bulkDeleteBtn.setOnAction(e -> {
            ObservableList<Facture> sel = getSelectedFactures();
            new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + sel.size() + " facture(s) ?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(r -> { if (r == ButtonType.YES) { controller.deleteMultiple(sel); refreshTable(); } });
        });

        Button closeBulkBtn = new Button("✕");
        closeBulkBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        closeBulkBtn.setOnAction(e -> { table.getSelectionModel().clearSelection(); bulkBar.setVisible(false); });

        bulkBar.getChildren().addAll(selectedCountLabel, bulkPayBtn, bulkDeleteBtn, closeBulkBtn);

        // ========== PAGINATION ==========
        HBox pagination = new HBox(12);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        Button prevBtn = new Button("◀");
        Button nextBtn = new Button("▶");
        String pageBtnStyle = "-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-min-width: 38; -fx-min-height: 38; -fx-cursor: hand;";
        prevBtn.setStyle(pageBtnStyle);
        nextBtn.setStyle(pageBtnStyle);
        pageLabel.setFont(Font.font(14));

        prevBtn.setOnAction(e -> { if (currentPage > 1) { currentPage--; refreshTable(); } });
        nextBtn.setOnAction(e -> {
            int totalPages = Math.max(1, (int) Math.ceil((double) controller.getFilteredFactures().size() / rowsPerPage));
            if (currentPage < totalPages) { currentPage++; refreshTable(); }
        });

        pagination.getChildren().addAll(prevBtn, pageLabel, nextBtn);

        root.getChildren().addAll(header, statsGrid, filterBar, table, bulkBar, pagination);
        applyFiltersAndRefresh();
        return root;
    }

    // ==================== HELPERS ====================

    private Button createActionBtn(String icon, String color) {
        Button btn = new Button(icon);
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-min-width: 30px;" +
            "-fx-min-height: 30px;" +
            "-fx-max-width: 30px;" +
            "-fx-max-height: 30px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0;" +
            "-fx-alignment: center;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-min-width: 30px;" +
            "-fx-min-height: 30px;" +
            "-fx-max-width: 30px;" +
            "-fx-max-height: 30px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0;" +
            "-fx-alignment: center;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-min-width: 30px;" +
            "-fx-min-height: 30px;" +
            "-fx-max-width: 30px;" +
            "-fx-max-height: 30px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0;" +
            "-fx-alignment: center;"
        ));
        return btn;
    }

    private VBox createStatCard(String icon, String title, String value, String color, String filter) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: white; -fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 20px; -fx-background-radius: 20px;" +
            "-fx-cursor: hand; -fx-min-width: 180;"
        );
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(28));
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        card.setOnMouseClicked(e -> { currentFilter = filter; applyFiltersAndRefresh(); });
        return card;
    }

    private ToggleButton createFilterTab(String text) {
        ToggleButton btn = new ToggleButton(text);
        btn.setStyle(
            "-fx-background-color: #f1f5f9; -fx-text-fill: #334155;" +
            "-fx-background-radius: 30px; -fx-padding: 8 22;" +
            "-fx-font-size: 13px; -fx-font-weight: 600; -fx-cursor: hand;"
        );
        btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) btn.setStyle(
                "-fx-background-color: #E66239; -fx-text-fill: white;" +
                "-fx-background-radius: 30px; -fx-padding: 8 22;" +
                "-fx-font-size: 13px; -fx-font-weight: 600;"
            );
            else btn.setStyle(
                "-fx-background-color: #f1f5f9; -fx-text-fill: #334155;" +
                "-fx-background-radius: 30px; -fx-padding: 8 22;" +
                "-fx-font-size: 13px; -fx-font-weight: 600;"
            );
        });
        return btn;
    }

    private void refreshTable() { applyFiltersAndRefresh(); }

    private void applyFiltersAndRefresh() {
        String dateStr = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
        controller.applyFilter(currentFilter, searchField.getText(), dateStr);
        table.refresh();
        int totalPages = Math.max(1, (int) Math.ceil((double) controller.getFilteredFactures().size() / rowsPerPage));
        pageLabel.setText("Page " + currentPage + " / " + totalPages);
        updateBulkBar();
    }

    private void updateBulkBar() {
        ObservableList<Facture> sel = getSelectedFactures();
        bulkBar.setVisible(sel.size() >= 2);
        selectedCountLabel.setText(sel.size() + " sélectionnée(s)");
    }

    private ObservableList<Facture> getSelectedFactures() {
        return FXCollections.observableArrayList(table.getSelectionModel().getSelectedItems());
    }

    private String formatMoney(double amount) { return String.format("%,.0f FCFA", amount); }
}