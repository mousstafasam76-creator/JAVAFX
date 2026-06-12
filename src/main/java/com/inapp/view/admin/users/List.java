package com.inapp.view.admin.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.AlertUtils;
import com.inapp.model.User;

public class List extends VBox {
    
    private NavigationManager navigationManager;
    private TableView<User> tableView;
    private ObservableList<User> userList;
    private Label totalUsersLabel;
    private Label totalActiveLabel;
    
    public List(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.userList = FXCollections.observableArrayList();
        setupUI();
        loadData();
        updateStats();
    }
    
    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #f0f2f5;");
        
        Label title = new Label("Gestion des Utilisateurs");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        HBox statsBox = createStatsCards();
        
        HBox searchBox = new HBox(15);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        searchField.setPrefWidth(300);
        
        Button addBtn = new Button("+ Nouvel utilisateur");
        addBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        addBtn.setOnAction(e -> navigationManager.navigateTo("usersAdd"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchField, spacer, addBtn);
        
        tableView = new TableView<>();
        
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Utilisateur");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(120);
        
        TableColumn<User, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<User, Void>() {
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
                    User user = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> navigationManager.navigateTo("usersEdit?id=" + user.getId()));
                    deleteBtn.setOnAction(e -> {
                        if (AlertUtils.confirmDelete("Supprimer " + user.getUsername() + " ?")) {
                            userList.remove(user);
                            updateStats();
                        }
                    });
                    setGraphic(buttons);
                }
            }
        });
        
        tableView.getColumns().addAll(idCol, usernameCol, emailCol, roleCol, statusCol, actionsCol);
        
        getChildren().addAll(title, statsBox, searchBox, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(20);
        
        VBox card1 = new VBox(10);
        card1.setPadding(new Insets(20));
        card1.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        card1.setPrefWidth(200);
        Label card1Title = new Label("Total Utilisateurs");
        totalUsersLabel = new Label("0");
        totalUsersLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        card1.getChildren().addAll(card1Title, totalUsersLabel);
        
        VBox card2 = new VBox(10);
        card2.setPadding(new Insets(20));
        card2.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        card2.setPrefWidth(200);
        Label card2Title = new Label("Utilisateurs Actifs");
        totalActiveLabel = new Label("0");
        totalActiveLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        card2.getChildren().addAll(card2Title, totalActiveLabel);
        
        statsBox.getChildren().addAll(card1, card2);
        return statsBox;
    }
    
    private void loadData() {
        userList.add(new User(1, "admin", "admin@inapp.com", "super_admin", "actif"));
        userList.add(new User(2, "jean", "jean@inapp.com", "admin", "actif"));
        userList.add(new User(3, "marie", "marie@inapp.com", "admin", "inactif"));
        tableView.setItems(userList);
    }
    
    private void updateStats() {
        totalUsersLabel.setText(String.valueOf(userList.size()));
        long active = userList.stream().filter(u -> "actif".equals(u.getStatus())).count();
        totalActiveLabel.setText(String.valueOf(active));
    }
}