package com.inapp.view.front.Produit;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import com.inapp.model.Product;
import com.inapp.controller.front.ProductController;
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
    private TableView<Product> tableView;
    private Runnable onUpdate;
    private ProductController productController;

    public ProductViewApp() {
        this.productController = ProductController.getInstance();
        rootView = createView();
        loadData();
    }

    public Node getView() {
        return rootView;
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    private void loadData() {
        new Thread(() -> {
            try {
                productController.loadProducts();
                Platform.runLater(() -> {
                    products.setAll(productController.getProducts());
                    updateStatistics();
                    filterProducts();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur de chargement des produits: " + e.getMessage());
                    alert.showAndWait();
                });
                e.printStackTrace();
            }
        }).start();
    }

    public void refreshData() {
        loadData();
    }

    private Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        try {
            String resourcePath = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
            java.io.InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + imagePath + " - " + e.getMessage());
        }
        return null;
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

        Region spacer = new Region();
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
            CreateProductDialog dialog = new CreateProductDialog(products, this::refreshData);
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

        Region spacer = new Region();
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

        return card;
    }

    private HBox createFilters() {
        HBox filters = new HBox(12);
        filters.setPadding(new Insets(0, 24, 16, 24));
        filters.setAlignment(Pos.CENTER_LEFT);

        categoryFilter = new ComboBox<>();
        // Charger les catégories depuis le contrôleur
        ObservableList<String> categories = productController.getCategories();
        categoryFilter.getItems().add("Toutes les catégories");
        categoryFilter.getItems().addAll(categories);
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
        Label countLabel = new Label("(" + products.size() + " produit(s) au total)");
        countLabel.setTextFill(Color.web("#666"));
        countLabel.setFont(Font.font(11));
        countLabel.setPadding(new Insets(0, 0, 0, 10));

        tableHeader.getChildren().addAll(tableTitle, countLabel);

        tableView = new TableView<>(filteredProducts);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: white; -fx-border-color: transparent;");

        // ========== COLONNE IMAGE ==========
        TableColumn<Product, String> imageCol = new TableColumn<>("Image");
        imageCol.setPrefWidth(80);
        imageCol.setCellFactory(col -> new TableCell<Product, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-background-radius: 4px; -fx-border-radius: 4px;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                    return;
                }

                Image image = loadImage(item);
                if (image != null) {
                    imageView.setImage(image);
                    setGraphic(imageView);
                } else {
                    VBox placeholder = new VBox(2);
                    placeholder.setAlignment(Pos.CENTER);
                    Label icon = new Label("📷");
                    icon.setFont(Font.font(20));
                    placeholder.getChildren().add(icon);
                    setGraphic(placeholder);
                }
            }
        });
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        // ========== COLONNE NOM ==========
        TableColumn<Product, String> nameCol = new TableColumn<>("Nom du produit");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        // ========== COLONNE DESCRIPTION ==========
        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        // ========== COLONNE PRIX ==========
        TableColumn<Product, Number> priceCol = new TableColumn<>("Prix (FCFA)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
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

        // ========== COLONNE QUANTITÉ ==========
        TableColumn<Product, Number> stockCol = new TableColumn<>("Quantité");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        stockCol.setPrefWidth(100);
        stockCol.setCellFactory(col -> new TableCell<Product, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    int value = item.intValue();
                    if (value == 0) {
                        setTextFill(Color.RED);
                    } else if (value < 5) {
                        setTextFill(Color.ORANGE);
                    } else {
                        setTextFill(Color.GREEN);
                    }
                    setText(String.valueOf(value));
                }
            }
        });

        // ========== COLONNE CATÉGORIE ==========
        TableColumn<Product, String> catCol = new TableColumn<>("Catégorie");
        catCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        catCol.setPrefWidth(130);

        // ========== COLONNE CRÉATEUR ==========
        TableColumn<Product, String> creatorCol = new TableColumn<>("Créé par");
        creatorCol.setCellValueFactory(new PropertyValueFactory<>("creator"));
        creatorCol.setPrefWidth(110);

        // ========== COLONNE DATE ==========
        TableColumn<Product, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        // ========== COLONNE ACTIONS ==========
        TableColumn<Product, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button viewBtn = new Button("👁️");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox buttons = new HBox(6);

            {
                viewBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showAlert("Détails du produit", 
                        "Nom: " + product.getName() + 
                        "\nDescription: " + product.getDescription() +
                        "\nPrix: " + String.format("%,d", (int)product.getPrice()) + " FCFA" +
                        "\nStock: " + product.getStock() +
                        "\nCatégorie: " + product.getCategoryName());
                });
                editBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    EditProductDialog dialog = new EditProductDialog(product, () -> {
                        updateStatistics();
                        filterProducts();
                        if (onUpdate != null) onUpdate.run();
                    });
                    dialog.showAndWait();
                });
                deleteBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    DeleteProductDialog dialog = new DeleteProductDialog(product, products, () -> {
                        updateStatistics();
                        filterProducts();
                        if (onUpdate != null) onUpdate.run();
                    });
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

        tableView.getColumns().addAll(imageCol, nameCol, descCol, priceCol, stockCol, catCol, creatorCol, dateCol, actionsCol);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e9ecef; -fx-border-width: 1px;");
        card.getChildren().addAll(tableHeader, tableView);

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
            boolean matchSearch = search.isEmpty() || 
                product.getName().toLowerCase().contains(search) ||
                product.getDescription().toLowerCase().contains(search);
            boolean matchCategory = category.equals("Toutes les catégories") || 
                (product.getCategoryName() != null && product.getCategoryName().equals(category));
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
                default:
                    break;
            }
        }
        
        int count = filteredProducts.size();
        try {
            VBox card = (VBox) tableView.getParent();
            HBox header = (HBox) card.getChildren().get(0);
            Label countLabel = (Label) header.getChildren().get(1);
            countLabel.setText("(" + count + " produit(s) au total)");
        } catch (Exception e) {
            // Ignorer les erreurs
        }
        
        updateStatistics();
    }

    private void resetFilters() {
        searchField.clear();
        categoryFilter.setValue("Toutes les catégories");
        sortFilter.setValue("Plus récent");
        filterProducts();
    }

    private void updateStatistics() {
        int totalProducts = products.size();
        int totalQuantity = products.stream().mapToInt(p -> p.getStock()).sum();
        double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();
        long lowStock = products.stream().filter(p -> p.getStock() < 5 && p.getStock() > 0).count();

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