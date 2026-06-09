package com.inapp.view.admin.products;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Product;

public class List extends VBox {
    
    private NavigationManager navigationManager;
    private TableView<Product> tableView;
    private ObservableList<Product> productList;
    private Label totalProductsLabel;
    private Label totalValueLabel;
    
    public List(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.productList = FXCollections.observableArrayList();
        setupUI();
        loadData();
        updateStats();
    }
    
    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #f0f2f5;");
        
        Label title = new Label("Gestion des Produits");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        HBox statsBox = createStatsCards();
        
        HBox searchBox = new HBox(15);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        searchField.setPrefWidth(300);
        
        Button addBtn = new Button("+ Nouveau produit");
        addBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        addBtn.setOnAction(e -> navigationManager.navigateTo("productsAdd"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchField, spacer, addBtn);
        
        tableView = new TableView<>();
        
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Produit");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Categorie");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryCol.setPrefWidth(150);
        
        TableColumn<Product, Double> priceCol = new TableColumn<>("Prix");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(120);
        
        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantite");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<Product, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #ffc107; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
                buttons.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    Product product = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> navigationManager.navigateTo("productsEdit?id=" + product.getId()));
                    deleteBtn.setOnAction(e -> {
                        if (AlertUtils.confirmDelete("Supprimer " + product.getName() + " ?")) {
                            productList.remove(product);
                            updateStats();
                        }
                    });
                    setGraphic(buttons);
                }
            }
        });
        
        tableView.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, quantityCol, actionsCol);
        
        getChildren().addAll(title, statsBox, searchBox, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(20);
        
        VBox card1 = new VBox(10);
        card1.setPadding(new Insets(20));
        card1.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        card1.setPrefWidth(200);
        Label card1Title = new Label("Total Produits");
        totalProductsLabel = new Label("0");
        totalProductsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        card1.getChildren().addAll(card1Title, totalProductsLabel);
        
        VBox card2 = new VBox(10);
        card2.setPadding(new Insets(20));
        card2.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        card2.setPrefWidth(200);
        Label card2Title = new Label("Valeur du stock");
        totalValueLabel = new Label("0 FCFA");
        totalValueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        card2.getChildren().addAll(card2Title, totalValueLabel);
        
        statsBox.getChildren().addAll(card1, card2);
        return statsBox;
    }
    
    private void loadData() {
        productList.add(new Product(1, "iPhone 15 Pro", "Electronique", 1200000, 15));
        productList.add(new Product(2, "MacBook Pro", "Electronique", 2500000, 8));
        productList.add(new Product(3, "AirPods Pro", "Electronique", 250000, 25));
        productList.add(new Product(4, "T-Shirt Premium", "Mode", 15000, 50));
        productList.add(new Product(5, "Jean Slim Fit", "Mode", 35000, 30));
        tableView.setItems(productList);
    }
    
    private void updateStats() {
        totalProductsLabel.setText(String.valueOf(productList.size()));
        double total = productList.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        totalValueLabel.setText(String.format("%,.0f FCFA", total));
    }
}