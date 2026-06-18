package com.inapp.view.front.Produit;

import com.inapp.controller.front.ProduitController;
import com.inapp.model.Product;
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
import com.inapp.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductViewApp {

    private final ProduitController controller;
    private final ObservableList<Product> products;
    private final FilteredList<Product> filteredProducts;
    private VBox rootView;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> sortFilter;
    private Label totalProductsLabel;
    private Label totalQuantityLabel;
    private Label totalValueLabel;
    private Label lowStockLabel;
    private javafx.scene.control.TableView<Product> tableView;
    private Label countLabel;

    public ProductViewApp() {
        controller = new ProduitController();
        products = controller.getProducts();
        filteredProducts = new FilteredList<>(products, p -> true);
        rootView = createView();
    }

    public Node getView() {
        return rootView;
    }

    public void initializeAsComponent() {}

    private List<String> loadCategories() {
        List<String> cats = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nomcat FROM categories ORDER BY nomcat")) {
            while (rs.next()) {
                cats.add(rs.getString("nomcat"));
            }
        } catch (Exception e) {
            cats.add("telephone");
            cats.add("TV");
            cats.add("accessoires");
            cats.add("Son et audio");
            cats.add("Ordinateur");
        }
        return cats;
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

        updateStatistics();

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
        Label subtitle = new Label("G\u00E9rez tous vos produits depuis cet interface");
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
            CreateProductDialog dialog = new CreateProductDialog(controller);
            dialog.showAndWait();
            updateStatistics();
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
            createStatCard("Total produits", totalProductsLabel, "\uD83D\uDCE6", "rgba(230,98,57,0.1)"),
            createStatCard("Quantit\u00E9 totale", totalQuantityLabel, "\uD83D\uDCCB", "rgba(243,156,18,0.1)"),
            createStatCard("Valeur totale stock", totalValueLabel, "\uD83D\uDCB0", "rgba(40,167,69,0.1)"),
            createStatCard("Stock faible", lowStockLabel, "\u26A0\uFE0F", "rgba(220,53,69,0.1)")
        );

        return cards;
    }

    private VBox createStatCard(String title, Label valueLabel, String icon, String bgColor) {
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
        categoryFilter.getItems().add("Toutes les cat\u00E9gories");
        categoryFilter.getItems().addAll(loadCategories());
        categoryFilter.setValue("Toutes les cat\u00E9gories");
        categoryFilter.setPrefWidth(200);
        categoryFilter.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        categoryFilter.setOnAction(e -> filterProducts());

        sortFilter = new ComboBox<>();
        sortFilter.getItems().addAll(
            "Plus r\u00E9cent", "Plus ancien", "Nom (A-Z)", "Nom (Z-A)",
            "Prix (croissant)", "Prix (d\u00E9croissant)", "Stock (croissant)", "Stock (d\u00E9croissant)"
        );
        sortFilter.setValue("Plus r\u00E9cent");
        sortFilter.setPrefWidth(200);
        sortFilter.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        sortFilter.setOnAction(e -> filterProducts());

        Button resetButton = new Button("\u21BB R\u00E9initialiser");
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

        Label tableTitle = new Label("\uD83D\uDCE6 Liste des produits");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        countLabel = new Label("(0 produit(s) au total)");
        countLabel.setTextFill(Color.web("#666"));
        countLabel.setFont(Font.font(11));
        countLabel.setPadding(new Insets(0, 0, 0, 10));

        tableHeader.getChildren().addAll(tableTitle, countLabel);

        tableView = new javafx.scene.control.TableView<>(filteredProducts);
        tableView.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: white; -fx-border-color: transparent;");

        javafx.scene.control.TableColumn<Product, String> nameCol = new javafx.scene.control.TableColumn<>("Nom du produit");
        nameCol.setCellValueFactory(data -> data.getValue().nompProperty());
        nameCol.setPrefWidth(180);

        javafx.scene.control.TableColumn<Product, String> descCol = new javafx.scene.control.TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descCol.setPrefWidth(200);

        javafx.scene.control.TableColumn<Product, Number> priceCol = new javafx.scene.control.TableColumn<>("Prix (FCFA)");
        priceCol.setCellValueFactory(data -> data.getValue().prixProperty());
        priceCol.setPrefWidth(120);
        priceCol.setCellFactory(col -> new javafx.scene.control.TableCell<Product, Number>() {
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

        javafx.scene.control.TableColumn<Product, Number> stockCol = new javafx.scene.control.TableColumn<>("Quantit\u00E9");
        stockCol.setCellValueFactory(data -> data.getValue().quantiteProperty());
        stockCol.setPrefWidth(100);

        javafx.scene.control.TableColumn<Product, String> catCol = new javafx.scene.control.TableColumn<>("Cat\u00E9gorie");
        catCol.setCellValueFactory(data -> data.getValue().categorieNomProperty());
        catCol.setPrefWidth(130);

        javafx.scene.control.TableColumn<Product, String> dateCol = new javafx.scene.control.TableColumn<>("Date cr\u00E9ation");
        dateCol.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getCreatedAt().toLocalDate().toString()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        dateCol.setPrefWidth(120);

        javafx.scene.control.TableColumn<Product, Void> actionsCol = new javafx.scene.control.TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new javafx.scene.control.TableCell<Product, Void>() {
            private final Button viewBtn = new Button("\uD83D\uDC41");
            private final Button editBtn = new Button("\u270F\uFE0F");
            private final Button deleteBtn = new Button("\uD83D\uDDD1");
            private final HBox buttons = new HBox(6);

            {
                viewBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 8px; -fx-background-radius: 4px; -fx-cursor: hand;");

                buttons.setAlignment(Pos.CENTER);
                buttons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                    Product product = getTableView().getItems().get(getIndex());
                    viewBtn.setOnAction(e -> showAlert("D\u00E9tails du produit",
                        "Nom: " + product.getNomp() +
                        "\nPrix: " + product.getPrix() + " FCFA" +
                        "\nStock: " + product.getQuantite() +
                        "\nDescription: " + product.getDescription()));
                    editBtn.setOnAction(e -> {
                        EditProductDialog dialog = new EditProductDialog(product, controller);
                        dialog.showAndWait();
                        updateStatistics();
                    });
                    deleteBtn.setOnAction(e -> {
                        DeleteProductDialog dialog = new DeleteProductDialog(product, controller);
                        dialog.showAndWait();
                        updateStatistics();
                    });
                }
            }
        });

        tableView.getColumns().addAll(nameCol, descCol, priceCol, stockCol, catCol, dateCol, actionsCol);
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
        Label copyright = new Label("Copyright \u00A9 2026 InApp Inventory Dashboard.");
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
            boolean matchSearch = search.isEmpty() || product.getNomp().toLowerCase().contains(search);
            boolean matchCategory = category.equals("Toutes les cat\u00E9gories") || product.getCategorieNom().equals(category);
            return matchSearch && matchCategory;
        });

        if (sort != null) {
            switch (sort) {
                case "Plus r\u00E9cent":
                    products.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
                    break;
                case "Plus ancien":
                    products.sort(Comparator.comparingInt(Product::getId));
                    break;
                case "Nom (A-Z)":
                    products.sort(Comparator.comparing(Product::getNomp, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Nom (Z-A)":
                    products.sort(Comparator.comparing(Product::getNomp, String.CASE_INSENSITIVE_ORDER).reversed());
                    break;
                case "Prix (croissant)":
                    products.sort(Comparator.comparingInt(Product::getPrix));
                    break;
                case "Prix (d\u00E9croissant)":
                    products.sort((a, b) -> Integer.compare(b.getPrix(), a.getPrix()));
                    break;
                case "Stock (croissant)":
                    products.sort(Comparator.comparingInt(Product::getQuantite));
                    break;
                case "Stock (d\u00E9croissant)":
                    products.sort((a, b) -> Integer.compare(b.getQuantite(), a.getQuantite()));
                    break;
            }
        }
    }

    private void resetFilters() {
        searchField.clear();
        categoryFilter.setValue("Toutes les cat\u00E9gories");
        sortFilter.setValue("Plus r\u00E9cent");
        filterProducts();
    }

    private void updateStatistics() {
        int totalProducts = products.size();
        int totalQuantity = products.stream().mapToInt(Product::getQuantite).sum();
        double totalValue = products.stream().mapToDouble(p -> (double)p.getPrix() * p.getQuantite()).sum();
        long lowStock = products.stream().filter(p -> p.getQuantite() < 5).count();

        totalProductsLabel.setText(String.valueOf(totalProducts));
        totalQuantityLabel.setText(String.valueOf(totalQuantity));
        totalValueLabel.setText(String.format("%,.0f FCFA", totalValue));
        lowStockLabel.setText(String.valueOf(lowStock));
        countLabel.setText("(" + totalProducts + " produit(s) au total)");
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