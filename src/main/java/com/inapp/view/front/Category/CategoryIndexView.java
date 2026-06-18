package com.inapp.view.front.Category;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import com.inapp.view.front.Dashboard;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CategoryIndexView extends VBox {

    public static class CategoryDisplay {
        public com.inapp.model.Category model;
        public String imageUrl;
        public int productCount;
        public double totalValue;
        public String priceDisplay;

        public CategoryDisplay(com.inapp.model.Category model, String imageUrl, int productCount, double totalValue, String priceDisplay) {
            this.model = model;
            this.imageUrl = imageUrl;
            this.productCount = productCount;
            this.totalValue = totalValue;
            this.priceDisplay = priceDisplay;
        }
    }

    private List<CategoryDisplay> allCategories;
    private FilteredList<CategoryDisplay> filteredCategories;
    private SortedList<CategoryDisplay> sortedCategories;
    private TilePane cardsContainer;
    private TextField searchField;
    private ComboBox<String> sortCombo;
    private ToggleGroup filterGroup;
    private Label pageInfo;
    private Button prevBtn, nextBtn;
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 9; // 3 colonnes x 3 lignes
    private Dashboard dashboard;
    private VBox notificationPanel;

    public CategoryIndexView(Dashboard dashboard) {
        System.out.println("✅✅✅ NOUVELLE VUE CategoryIndexView CHARGÉE ✅✅✅");
        this.dashboard = dashboard;
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f7fa;");

        notificationPanel = new VBox(8);
        notificationPanel.setAlignment(Pos.TOP_RIGHT);
        notificationPanel.setPadding(new Insets(10));
        notificationPanel.setPickOnBounds(false);
        StackPane.setAlignment(notificationPanel, Pos.TOP_RIGHT);

        VBox headerBox = createHeader();

        cardsContainer = new TilePane();
        cardsContainer.setHgap(12);
        cardsContainer.setVgap(12);
        cardsContainer.setPadding(new Insets(10));
        cardsContainer.setPrefColumns(3);

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        HBox paginationBox = createPagination();

        getChildren().addAll(headerBox, scrollPane, paginationBox);

        sceneProperty().addListener((obs, old, newScene) -> {
            if (newScene != null) {
                StackPane root = (StackPane) newScene.getRoot();
                root.getChildren().add(notificationPanel);
            }
        });

        chargerCategoriesParDefaut();
        applyFiltersAndSort();
        updatePage();
        afficherNotificationsRejetees();
    }

    private VBox createHeader() {
        VBox headerBox = new VBox(15);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Catégories");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Rechercher une catégorie...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-padding: 8; -fx-background-radius: 20;");
        searchField.textProperty().addListener((obs, old, val) -> {
            currentPage = 0;
            applyFiltersAndSort();
            updatePage();
        });

        sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Nom (A-Z)", "Nom (Z-A)", "Produits (croissant)", "Produits (décroissant)", "Valeur (croissante)", "Valeur (décroissante)");
        sortCombo.setValue("Nom (A-Z)");
        sortCombo.setOnAction(e -> {
            currentPage = 0;
            applyFiltersAndSort();
            updatePage();
        });

        Button addButton = new Button("+ Nouvelle catégorie");
        addButton.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 20; -fx-cursor: hand;");
        addButton.setOnAction(e -> dashboard.showCategoryCreate());

        topRow.getChildren().addAll(title, spacer, searchField, sortCombo, addButton);

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterGroup = new ToggleGroup();

        ToggleButton allBtn = createFilterButton("Toutes", null);
        ToggleButton pendingBtn = createFilterButton("En attente", "pending");
        ToggleButton approvedBtn = createFilterButton("Validées", "approved");
        ToggleButton rejectedBtn = createFilterButton("Rejetées", "rejected");
        allBtn.setSelected(true);

        filterRow.getChildren().addAll(allBtn, pendingBtn, approvedBtn, rejectedBtn);
        headerBox.getChildren().addAll(topRow, filterRow);
        return headerBox;
    }

    private ToggleButton createFilterButton(String text, String statusValue) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(filterGroup);
        btn.setUserData(statusValue);
        btn.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #666; -fx-background-radius: 8; -fx-padding: 6 14;");
        btn.selectedProperty().addListener((obs, wasSel, isSel) -> {
            if (isSel) {
                currentPage = 0;
                applyFiltersAndSort();
                updatePage();
            }
        });
        return btn;
    }

    private HBox createPagination() {
        HBox paginationBox = new HBox(15);
        paginationBox.setAlignment(Pos.CENTER);
        prevBtn = new Button("◀ Précédent");
        nextBtn = new Button("Suivant ▶");
        pageInfo = new Label("Page 0 / 0");
        prevBtn.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });
        nextBtn.setOnAction(e -> {
            int maxPage = (int) Math.ceil((double) sortedCategories.size() / ITEMS_PER_PAGE) - 1;
            if (currentPage < maxPage) {
                currentPage++;
                updatePage();
            }
        });
        paginationBox.getChildren().addAll(prevBtn, pageInfo, nextBtn);
        return paginationBox;
    }

    private void chargerCategoriesParDefaut() {
        allCategories = new ArrayList<>();
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(1, "Accessoire", "Câbles, chargeurs, étuis", "approved"),
            "/images/product-1.png", 12, 125000, "15 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(2, "Ordinateur", "PC portables, fixes", "approved"),
            "/images/product-2.png", 8, 980000, "450 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(3, "Son et Audio", "Enceintes, écouteurs", "pending"),
            "/images/product-3.png", 5, 210000, "85 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(4, "TV", "Téléviseurs", "rejected"),
            "/images/product-4.png", 4, 450000, "320 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(5, "Téléphone", "Smartphones", "approved"),
            "/images/product-5.png", 7, 680000, "220 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(6, "Gaming", "Consoles, jeux", "approved"),
            "/images/product-6.png", 3, 150000, "50 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(7, "Maison", "Électroménager", "pending"),
            "/images/product-7.png", 6, 320000, "45 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(8, "Sport", "Montres, équipements", "rejected"),
            "/images/product-8.png", 2, 98000, "25 000 FCFA"
        ));
        allCategories.add(new CategoryDisplay(
            new com.inapp.model.Category(9, "Bureau", "Imprimantes, papeterie", "approved"),
            "/images/product-9.png", 4, 175000, "30 000 FCFA"
        ));
        for (int i = 0; i < allCategories.size(); i++) {
            allCategories.get(i).model.setProductsCount((i + 1) * 3);
        }
    }

    private void applyFiltersAndSort() {
        String search = searchField.getText().toLowerCase();
        Predicate<CategoryDisplay> searchPredicate = data ->
            data.model.getName().toLowerCase().contains(search) ||
            data.model.getDescription().toLowerCase().contains(search);

        String selectedStatus = null;
        ToggleButton selected = (ToggleButton) filterGroup.getSelectedToggle();
        if (selected != null && selected.getUserData() != null) {
            selectedStatus = (String) selected.getUserData();
        }
        final String finalSelectedStatus = selectedStatus;
        Predicate<CategoryDisplay> statusPredicate = data -> {
            if (finalSelectedStatus == null) return true;
            return finalSelectedStatus.equals(data.model.getStatus());
        };

        filteredCategories = new FilteredList<>(
            FXCollections.observableArrayList(allCategories),
            searchPredicate.and(statusPredicate)
        );

        String sortType = sortCombo.getValue();
        Comparator<CategoryDisplay> comparator = null;
        if ("Nom (A-Z)".equals(sortType))
            comparator = Comparator.comparing((CategoryDisplay d) -> d.model.getName(), String.CASE_INSENSITIVE_ORDER);
        else if ("Nom (Z-A)".equals(sortType))
            comparator = Comparator.comparing((CategoryDisplay d) -> d.model.getName(), String.CASE_INSENSITIVE_ORDER).reversed();
        else if ("Produits (croissant)".equals(sortType))
            comparator = Comparator.comparingInt((CategoryDisplay d) -> d.productCount);
        else if ("Produits (décroissant)".equals(sortType))
            comparator = Comparator.comparingInt((CategoryDisplay d) -> d.productCount).reversed();
        else if ("Valeur (croissante)".equals(sortType))
            comparator = Comparator.comparingDouble((CategoryDisplay d) -> d.totalValue);
        else if ("Valeur (décroissante)".equals(sortType))
            comparator = Comparator.comparingDouble((CategoryDisplay d) -> d.totalValue).reversed();

        if (comparator != null)
            sortedCategories = new SortedList<>(filteredCategories, comparator);
        else
            sortedCategories = new SortedList<>(filteredCategories);
    }

    private void updatePage() {
        int fromIndex = currentPage * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, sortedCategories.size());
        List<CategoryDisplay> pageItems = sortedCategories.subList(fromIndex, toIndex);
        afficherCartes(pageItems);
        int totalPages = (int) Math.ceil((double) sortedCategories.size() / ITEMS_PER_PAGE);
        pageInfo.setText("Page " + (currentPage + 1) + " / " + Math.max(1, totalPages));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= totalPages - 1);
    }

    private void afficherCartes(List<CategoryDisplay> categoriesPage) {
        cardsContainer.getChildren().clear();
        for (CategoryDisplay data : categoriesPage) {
            VBox carte = creerCarte(data);
            cardsContainer.getChildren().add(carte);
            FadeTransition ft = new FadeTransition(Duration.millis(300), carte);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    // ========== CARTE COMPACTE (3 par ligne, boutons en texte complet) ==========
    private VBox creerCarte(CategoryDisplay data) {
        com.inapp.model.Category cat = data.model;
        VBox carte = new VBox(8);
        carte.setAlignment(Pos.TOP_CENTER);
        carte.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 14; -fx-background-radius: 14; " +
                "-fx-background-color: white; -fx-padding: 10; " +
                "-fx-min-width: 210; -fx-min-height: 260; " +
                "-fx-max-width: 240; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");

        // Animation survol
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), carte);
        scaleIn.setToX(1.02);
        scaleIn.setToY(1.02);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), carte);
        scaleOut.setToX(1);
        scaleOut.setToY(1);
        carte.setOnMouseEntered(e -> scaleIn.playFromStart());
        carte.setOnMouseExited(e -> scaleOut.playFromStart());

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(130);
        imageView.setPreserveRatio(true);
        imageView.setImage(chargerImage(data.imageUrl));

        // Badge statut
        Label statusBadge = new Label();
        String status = cat.getStatus();
        if ("approved".equals(status)) {
            statusBadge.setText("✓ Validée");
            statusBadge.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 9px;");
        } else if ("pending".equals(status)) {
            statusBadge.setText("⏳ En attente");
            statusBadge.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 9px;");
        } else {
            statusBadge.setText("✗ Rejetée");
            statusBadge.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 9px;");
        }
        StackPane imageStack = new StackPane(imageView, statusBadge);
        StackPane.setAlignment(statusBadge, Pos.TOP_LEFT);
        StackPane.setMargin(statusBadge, new Insets(6));

        // Badge nombre de produits
        Label countBadge = new Label(data.productCount + " produits");
        countBadge.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 9px;");
        StackPane.setAlignment(countBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(countBadge, new Insets(6));
        imageStack.getChildren().add(countBadge);

        // Nom
        Label nomLabel = new Label(cat.getName());
        nomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nomLabel.setWrapText(true);
        nomLabel.setAlignment(Pos.CENTER);

        // Description
        Label descLabel = new Label(cat.getDescription());
        descLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 10px;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(30);

        // Prix
        Label priceLabel = new Label(data.priceDisplay);
        priceLabel.setStyle("-fx-text-fill: #E66239; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Statistiques
        HBox statsBox = new HBox(10);
        statsBox.setAlignment(Pos.CENTER);
        Label produitsLabel = new Label("📦 " + data.productCount);
        produitsLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 10px;");
        Label valeurLabel = new Label("💰 " + String.format("%,.0f", data.totalValue) + " FCFA");
        valeurLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 10px;");
        statsBox.getChildren().addAll(produitsLabel, valeurLabel);

        // ===== BOUTONS AVEC LEURS NOMS COMPLETS =====
        HBox buttonsBox = new HBox(6);
        buttonsBox.setAlignment(Pos.CENTER);

        Button voirBtn = new Button("Voir");
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");

        styleButton(voirBtn, "#28a745", "#20c997", 60);
        styleButton(modifierBtn, "#fd7e14", "#ffc107", 60);
        styleButton(supprimerBtn, "#dc3545", "#e74c3c", 60);

        voirBtn.setOnAction(e -> dashboard.showCategoryDetail(cat.getId()));
        modifierBtn.setOnAction(e -> dashboard.showCategoryEdit(cat.getId()));
        supprimerBtn.setOnAction(e -> dashboard.showCategoryDelete(cat.getId()));

        buttonsBox.getChildren().addAll(voirBtn, modifierBtn, supprimerBtn);

        carte.getChildren().addAll(imageStack, nomLabel, descLabel, priceLabel, statsBox, buttonsBox);
        return carte;
    }

    // Style des boutons avec texte complet
    private void styleButton(Button btn, String color1, String color2, double prefWidth) {
        btn.setPrefWidth(prefWidth);
        btn.setPrefHeight(28);
        btn.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + color1 + "; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 11px; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-radius: 20; " +
            "-fx-border-width: 1.5; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);"
        );

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(120), btn);
        scaleIn.setToX(1.08);
        scaleIn.setToY(1.08);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(120), btn);
        scaleOut.setToX(1);
        scaleOut.setToY(1);

        btn.setOnMouseEntered(e -> {
            scaleIn.playFromStart();
            btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + "); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11px; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: transparent; " +
                "-fx-border-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 4);"
            );
        });
        btn.setOnMouseExited(e -> {
            scaleOut.playFromStart();
            btn.setStyle(
                "-fx-background-color: white; " +
                "-fx-text-fill: " + color1 + "; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11px; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 20; " +
                "-fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);"
            );
        });
    }

    private Image chargerImage(String path) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is != null) return new Image(is);
            is = getClass().getResourceAsStream("/images/logo-icon2.png");
            if (is != null) return new Image(is);
        } catch (Exception e) {}
        return new Image("https://via.placeholder.com/130x100");
    }

    private void afficherNotificationsRejetees() {
        List<CategoryDisplay> rejected = allCategories.stream()
                .filter(d -> "rejected".equals(d.model.getStatus()))
                .collect(Collectors.toList());
        if (rejected.isEmpty()) return;
        notificationPanel.getChildren().clear();
        for (CategoryDisplay data : rejected) {
            VBox notif = createRejectedNotification(data);
            notificationPanel.getChildren().add(notif);
        }
    }

    private VBox createRejectedNotification(CategoryDisplay data) {
        VBox notif = new VBox(8);
        notif.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff9800; -fx-border-width: 0 0 0 4; -fx-background-radius: 8; -fx-padding: 12;");
        notif.setMaxWidth(360);
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("CATÉGORIE REJETÉE");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100; -fx-font-size: 12px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("×");
        closeBtn.setStyle("-fx-background: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), notif);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(ev -> notificationPanel.getChildren().remove(notif));
            ft.play();
        });
        header.getChildren().addAll(icon, title, spacer, closeBtn);

        Label message = new Label("La catégorie \"" + data.model.getName() + "\" a été rejetée.");
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 12px;");

        Label reason = new Label("Raison : Non conforme aux conditions.");
        reason.setStyle("-fx-font-size: 11px; -fx-text-fill: #bf360c; -fx-background-color: #fff8e1; -fx-padding: 6; -fx-border-color: #ff9800; -fx-border-width: 0 0 0 2;");

        HBox actions = new HBox(10);
        Button editBtn = new Button("Modifier");
        editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 4;");
        editBtn.setOnAction(e -> dashboard.showCategoryEdit(data.model.getId()));
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 4;");
        deleteBtn.setOnAction(e -> dashboard.showCategoryDelete(data.model.getId()));
        actions.getChildren().addAll(editBtn, deleteBtn);

        notif.getChildren().addAll(header, message, reason, actions);
        return notif;
    }
}