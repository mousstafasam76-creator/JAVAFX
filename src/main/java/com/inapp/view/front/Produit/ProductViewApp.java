package com.inapp.view.front.Produit;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.Comparator;

public class ProductViewApp {

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final FilteredList<Product> filteredProducts = new FilteredList<>(products, p -> true);
    private VBox rootView;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> sortFilter;
    private Label totalProductsLabel;
    private Label totalQuantityLabel;
    private Label totalValueLabel;
    private Label lowStockLabel;
    private javafx.scene.control.TableView<Product> tableView;

    public ProductViewApp() {
        rootView = createView();
        loadSampleData();
    }

    public Node getView() {
        return rootView;
    }

    public void initializeAsComponent() {}

    private void loadSampleData() {
        products.addAll(
            new Product(1, "Réfrigérateur Samsung", "Réfrigérateur double porte 450L", 350000, 15, "Électroménager", "Admin", "12/03/2026"),
            new Product(2, "Machine à laver LG", "Machine à laver 8kg automatique", 280000, 8, "Électroménager", "Admin", "10/03/2026"),
            new Product(3, "Climatiseur Panasonic", "Climatiseur split 12000 BTU", 220000, 3, "Climatisation", "Admin", "08/03/2026"),
            new Product(4, "Four micro-ondes Whirlpool", "Micro-ondes 30L grill", 95000, 22, "Cuisine", "Admin", "05/03/2026"),
            new Product(5, "Téléviseur Sony 55\"", "TV LED 4K Smart", 450000, 6, "Électronique", "Admin", "01/03/2026"),
            new Product(6, "Aspirateur Dyson", "Aspirateur sans fil V15", 180000, 0, "Électroménager", "Admin", "28/02/2026"),
            new Product(7, "Cafetière Delonghi", "Machine espresso automatique", 150000, 12, "Cuisine", "Admin", "25/02/2026"),
            new Product(8, "Congélateur Whirlpool", "Congélateur coffre 300L", 190000, 4, "Électroménager", "Admin", "20/02/2026")
        );
        updateStatistics();
    }

    private VBox createView() {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: #f8f9fa;");

        container.getChildren().add(createHeader());
        container.getChildren().add(createStatisticsCards());
        container.getChildren().add(createFilters());
        container.getChildren().add(createTable());
        container.getChildren().add(createFooter());

        VBox.setVgrow(container.getChildren().get(3), Priority.ALWAYS);

        return container;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(24, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");

        VBox titleBox = new VBox(4);
        Label title = new Label("Liste des produits");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1a1a2e"));
        Label subtitle = new Label("Gérez tous vos produits depuis cet interface");
        subtitle.setTextFill(Color.web("#6c757d"));
        subtitle.setFont(Font.font(12));
        titleBox.getChildren().addAll(title, subtitle);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");
        searchField.setPrefWidth(260);
        searchField.setStyle(
            "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8px;" +
            "-fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-font-size: 13px;"
        );
        searchField.textProperty().addListener((obs, old, val) -> filterProducts());

        Button createButton = new Button("+ Nouveau produit");
        createButton.setStyle(
            "-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-radius: 8px; -fx-cursor: hand;"
        );
        createButton.setOnMouseEntered(e -> createButton.setStyle(
            "-fx-background-color: #d4552e; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-radius: 8px; -fx-cursor: hand;"
        ));
        createButton.setOnMouseExited(e -> createButton.setStyle(
            "-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-radius: 8px; -fx-cursor: hand;"
        ));
        createButton.setOnAction(e -> {
            CreateProductDialog dialog = new CreateProductDialog(products, this::updateStatistics);
            dialog.showAndWait();
        });
        animateButton(createButton);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        searchBox.getChildren().addAll(searchField, createButton);

        header.getChildren().addAll(titleBox, spacer, searchBox);
        return header;
    }

    private HBox createStatisticsCards() {
        HBox cards = new HBox(16);
        cards.setPadding(new Insets(16, 24, 16, 24));
        cards.setAlignment(Pos.CENTER_LEFT);

        totalProductsLabel = new Label("0");
        totalQuantityLabel = new Label("0");
        totalValueLabel = new Label("0 FCFA");
        lowStockLabel = new Label("0");

        cards.getChildren().addAll(
            createStatCard("Total produits", totalProductsLabel, "📦", "rgba(230,98,57,0.1)", "#E66239"),
            createStatCard("Quantité totale", totalQuantityLabel, "📋", "rgba(243,156,18,0.1)", "#f39c12"),
            createStatCard("Valeur totale stock", totalValueLabel, "💰", "rgba(40,167,69,0.1)", "#28a745"),
            createStatCard("Stock faible", lowStockLabel, "⚠️", "rgba(220,53,69,0.1)", "#dc3545")
        );

        return cards;
    }

    private VBox createStatCard(String title, Label valueLabel, String icon, String bgColor, String iconColor) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setPrefWidth(240);
        card.setStyle(
            "-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1px;" +
            "-fx-border-radius: 10px; -fx-background-radius: 10px;"
        );

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        VBox textBox = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#6c757d"));
        titleLabel.setFont(Font.font(11));
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.web("#1a1a2e"));
        textBox.getChildren().addAll(titleLabel, valueLabel);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");
        VBox iconBox = new VBox(iconLabel);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefSize(44, 44);
        iconBox.setMinSize(44, 44);
        iconBox.setMaxSize(44, 44);
        iconBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12px;");

        topRow.getChildren().addAll(textBox, spacer, iconBox);
        card.getChildren().add(topRow);

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1px;" +
            "-fx-border-radius: 10px; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1px;" +
            "-fx-border-radius: 10px; -fx-background-radius: 10px;"
        ));

        return card;
    }

    private HBox createFilters() {
        HBox filters = new HBox(12);
        filters.setPadding(new Insets(0, 24, 16, 24));
        filters.setAlignment(Pos.CENTER_LEFT);

        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("Toutes les catégories", "Électroménager", "Climatisation", "Cuisine", "Électronique");
        categoryFilter.setValue("Toutes les catégories");
        categoryFilter.setPrefWidth(200);
        categoryFilter.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        categoryFilter.setOnAction(e -> filterProducts());

        sortFilter = new ComboBox<>();
        sortFilter.getItems().addAll(
            "Plus récent", "Plus ancien", "Nom (A-Z)", "Nom (Z-A)",
            "Prix (croissant)", "Prix (décroissant)", "Stock (croissant)", "Stock (décroissant)"
        );
        sortFilter.setValue("Plus récent");
        sortFilter.setPrefWidth(200);
        sortFilter.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        sortFilter.setOnAction(e -> filterProducts());

        Button resetButton = new Button("↻ Réinitialiser");
        resetButton.setStyle(
            "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 12px;" +
            "-fx-padding: 8px 14px; -fx-background-radius: 8px; -fx-cursor: hand;"
        );
        resetButton.setOnAction(e -> resetFilters());

        filters.getChildren().addAll(categoryFilter, sortFilter, resetButton);
        return filters;
    }

    @SuppressWarnings("unchecked")
    private VBox createTable() {
        VBox tableContainer = new VBox(0);
        tableContainer.setPadding(new Insets(0, 24, 16, 24));
        tableContainer.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(16, 20, 16, 20));
        tableHeader.setStyle("-fx-background-color: white; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0; -fx-background-radius: 10px 10px 0 0;");
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        Label tableTitle = new Label("📦 Liste des produits");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label countLabel = new Label("(8 produit(s) au total)");
        countLabel.setTextFill(Color.web("#666"));
        countLabel.setFont(Font.font(11));
        countLabel.setPadding(new Insets(0, 0, 0, 10));

        tableHeader.getChildren().addAll(tableTitle, countLabel);

        tableView = new javafx.scene.control.TableView<>(filteredProducts);
        tableView.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: white; -fx-border-color: transparent;");

        TableColumn<Product, String> nameCol = new TableColumn<>("Nom du produit");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        nameCol.setPrefWidth(180);

        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descCol.setPrefWidth(200);

        TableColumn<Product, Number> priceCol = new TableColumn<>("Prix (FCFA)");
        priceCol.setCellValueFactory(data -> data.getValue().priceProperty());
        priceCol.setPrefWidth(120);
        priceCol.setCellFactory(col -> new TableCell<Product, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d FCFA", item.intValue()));
                    setTextFill(Color.web("#E66239"));
                    setFont(Font.font("System", FontWeight.BOLD, 12));
                }
            }
        });

        TableColumn<Product, Number> stockCol = new TableColumn<>("Quantité");
        stockCol.setCellValueFactory(data -> data.getValue().stockProperty());
        stockCol.setPrefWidth(100);

        TableColumn<Product, String> catCol = new TableColumn<>("Catégorie");
        catCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        catCol.setPrefWidth(130);

        TableColumn<Product, String> creatorCol = new TableColumn<>("Créé par");
        creatorCol.setCellValueFactory(data -> data.getValue().creatorProperty());
        creatorCol.setPrefWidth(110);

        TableColumn<Product, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        dateCol.setPrefWidth(100);

        TableColumn<Product, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");
            private final HBox buttons = new HBox(6);

            {
                viewBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showAlert("Détails du produit", product.getName() + "\nPrix: " + product.getPrice() + " FCFA\nStock: " + product.getStock());
                });
                editBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    EditProductDialog dialog = new EditProductDialog(product, ProductViewApp.this::updateStatistics);
                    dialog.showAndWait();
                });
                deleteBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    DeleteProductDialog dialog = new DeleteProductDialog(product, products, ProductViewApp.this::updateStatistics);
                    dialog.showAndWait();
                });

                buttons.setAlignment(Pos.CENTER);
                buttons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        tableView.getColumns().addAll(nameCol, descCol, priceCol, stockCol, catCol, creatorCol, dateCol, actionsCol);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e9ecef; -fx-border-width: 1px;");
        card.getChildren().addAll(tableHeader, tableView);
        VBox.setVgrow(card, Priority.ALWAYS);

        tableContainer.getChildren().add(card);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        return tableContainer;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16));
        footer.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1px 0 0 0;");
        Label copyright = new Label("Copyright © 2026 InApp Inventory Dashboard.");
        copyright.setTextFill(Color.web("#6c757d"));
        copyright.setFont(Font.font(11));
        footer.getChildren().add(copyright);
        return footer;
    }

    private void filterProducts() {
        String search = searchField.getText().toLowerCase().trim();
        String category = categoryFilter.getValue();
        String sort = sortFilter.getValue();

        filteredProducts.setPredicate(product -> {
            boolean matchSearch = search.isEmpty() || product.getName().toLowerCase().contains(search);
            boolean matchCategory = category.equals("Toutes les catégories") || product.getCategory().equals(category);
            return matchSearch && matchCategory;
        });

        if (sort != null) {
            switch (sort) {
                case "Plus récent":
                    products.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
                    break;
                case "Plus ancien":
                    products.sort(Comparator.comparingInt(Product::getId));
                    break;
                case "Nom (A-Z)":
                    products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Nom (Z-A)":
                    products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                    break;
                case "Prix (croissant)":
                    products.sort(Comparator.comparingDouble(Product::getPrice));
                    break;
                case "Prix (décroissant)":
                    products.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                    break;
                case "Stock (croissant)":
                    products.sort(Comparator.comparingInt(Product::getStock));
                    break;
                case "Stock (décroissant)":
                    products.sort((a, b) -> Integer.compare(b.getStock(), a.getStock()));
                    break;
            }
        }
    }

    private void resetFilters() {
        searchField.clear();
        categoryFilter.setValue("Toutes les catégories");
        sortFilter.setValue("Plus récent");
        filterProducts();
    }

    private void updateStatistics() {
        int totalProducts = products.size();
        int totalQuantity = products.stream().mapToInt(Product::getStock).sum();
        double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();
        long lowStock = products.stream().filter(p -> p.getStock() < 5).count();

        totalProductsLabel.setText(String.valueOf(totalProducts));
        totalQuantityLabel.setText(String.valueOf(totalQuantity));
        totalValueLabel.setText(String.format("%,.0f FCFA", totalValue));
        lowStockLabel.setText(String.valueOf(lowStock));
    }

    private void animateButton(Button button) {
        button.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), button);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(0.95);
            st.setToY(0.95);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}