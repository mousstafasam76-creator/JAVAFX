package com.inapp.view.admin.categories;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.Category;

public class List extends VBox {
    
    private NavigationManager navigationManager;
    private TableView<Category> tableView;
    private ObservableList<Category> categoryList;
    private FilteredList<Category> filteredList;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private Label totalCategoriesLabel;
    private Label totalProductsLabel;
    private int currentPage = 1;
    private int rowsPerPage = 10;
    private HBox paginationBox;
    
    public List(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.categoryList = FXCollections.observableArrayList();
        setupUI();
        loadData();
        updateStats();
    }
    
    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #f0f2f5;");
        
        Label title = new Label("Gestion des Categories");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox statsBox = createStatsCards();
        HBox searchBox = createSearchSection();
        VBox tableCard = createTableCard();
        paginationBox = createPagination();
        
        getChildren().addAll(title, statsBox, searchBox, tableCard, paginationBox);
        
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
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(20);
        
        VBox card1 = new VBox(10);
        card1.setPadding(new Insets(20));
        card1.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        Label card1Title = new Label("Total Categories");
        card1Title.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        totalCategoriesLabel = new Label("0");
        totalCategoriesLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        card1.getChildren().addAll(card1Title, totalCategoriesLabel);
        
        VBox card2 = new VBox(10);
        card2.setPadding(new Insets(20));
        card2.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        Label card2Title = new Label("Produits associes");
        card2Title.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        totalProductsLabel = new Label("0");
        totalProductsLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        card2.getChildren().addAll(card2Title, totalProductsLabel);
        
        VBox card3 = new VBox(10);
        card3.setPadding(new Insets(20));
        card3.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        Label card3Title = new Label("En attente");
        card3Title.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        Label pendingLabel = new Label("0");
        pendingLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #ffc107;");
        card3.getChildren().addAll(card3Title, pendingLabel);
        
        statsBox.getChildren().addAll(card1, card2, card3);
        return statsBox;
    }
    
    private HBox createSearchSection() {
        HBox searchBox = new HBox(15);
        searchBox.setPadding(new Insets(15, 20, 15, 20));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        
        searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, old, val) -> filterCategories());
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tous", "Approuve", "En attente");
        statusFilter.setValue("Tous");
        statusFilter.setOnAction(e -> filterCategories());
        
        Button addBtn = new Button("+ Nouvelle categorie");
        addBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        addBtn.setOnAction(e -> navigationManager.navigateTo("categoriesAdd"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        searchBox.getChildren().addAll(searchField, statusFilter, spacer, addBtn);
        return searchBox;
    }
    
    private VBox createTableCard() {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        
        tableView = new TableView<>();
        
        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<Category, String> nameCol = new TableColumn<>("Categorie");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Category, Integer> productsCol = new TableColumn<>("Produits");
        productsCol.setCellValueFactory(new PropertyValueFactory<>("productsCount"));
        productsCol.setPrefWidth(100);
        
        TableColumn<Category, Double> valueCol = new TableColumn<>("Valeur");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("stockValue"));
        valueCol.setPrefWidth(150);
        
        TableColumn<Category, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Category, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<Category, Void>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #ffc107; -fx-padding: 5px 10px;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5px 10px;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    Category category = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> navigationManager.navigateTo("categoriesEdit?id=" + category.getId()));
                    deleteBtn.setOnAction(e -> {
                        if (AlertUtils.confirmDelete("Supprimer ?")) {
                            categoryList.remove(category);
                            updateStats();
                        }
                    });
                    setGraphic(buttons);
                }
            }
        });
        
        tableView.getColumns().addAll(idCol, nameCol, productsCol, valueCol, statusCol, actionsCol);
        
        card.getChildren().add(tableView);
        return card;
    }
    
    private HBox createPagination() {
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(15));
        return pagination;
    }
    
    private void loadData() {
        Category cat1 = new Category(1, "Electronique", "", "approuve");
        cat1.setProductsCount(12);
        cat1.setStockValue(1250000);
        
        Category cat2 = new Category(2, "Mode", "", "approuve");
        cat2.setProductsCount(8);
        cat2.setStockValue(450000);
        
        Category cat3 = new Category(3, "Maison", "", "en attente");
        cat3.setProductsCount(3);
        cat3.setStockValue(89000);
        
        categoryList.addAll(cat1, cat2, cat3);
        filteredList = new FilteredList<>(categoryList, p -> true);
        tableView.setItems(filteredList);
    }
    
    private void filterCategories() {
        String searchText = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        
        filteredList.setPredicate(category -> {
            if (!searchText.isEmpty() && !category.getName().toLowerCase().contains(searchText)) return false;
            if (!"Tous".equals(status) && !status.toLowerCase().equals(category.getStatus())) return false;
            return true;
        });
        
        updateStats();
    }
    
    private void updateStats() {
        totalCategoriesLabel.setText(String.valueOf(filteredList.size()));
        int total = filteredList.stream().mapToInt(Category::getProductsCount).sum();
        totalProductsLabel.setText(String.valueOf(total));
    }
    
    private void refreshData() {
        filterCategories();
    }
}