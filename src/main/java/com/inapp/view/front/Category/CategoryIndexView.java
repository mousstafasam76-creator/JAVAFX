package com.inapp.view.front.Category;

import javafx.animation.FadeTransition;
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
import com.inapp.utils.NavigationManager;
import com.inapp.model.Category;
import com.inapp.controller.front.CategoryController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryIndexView extends VBox {

    public static class CategoryDisplay {
        public Category model;
        public String imageUrl;
        public int productCount;
        public double totalValue;
        public String priceDisplay;

        public CategoryDisplay(Category model, String imageUrl, int productCount, double totalValue, String priceDisplay) {
            this.model = model;
            this.imageUrl = imageUrl;
            this.productCount = productCount;
            this.totalValue = totalValue;
            this.priceDisplay = priceDisplay;
        }
    }

    private NavigationManager navigationManager;
    private CategoryController categoryController;
    private List<CategoryDisplay> allCategories = new ArrayList<>();
    private FilteredList<CategoryDisplay> filteredCategories;
    private SortedList<CategoryDisplay> sortedCategories;
    private TilePane cardsContainer;
    private TextField searchField;
    private ComboBox<String> sortCombo;
    private ToggleGroup filterGroup;
    private Label pageInfo;
    private Button prevBtn, nextBtn;
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 9;
    private VBox notificationPanel;
    private Button addButton;

    public CategoryIndexView(NavigationManager navManager) {
        System.out.println("✅ CategoryIndexView CHARGEE");
        this.navigationManager = navManager;
        this.categoryController = CategoryController.getInstance();
        
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f7fa;");

        notificationPanel = new VBox(8);
        notificationPanel.setAlignment(Pos.TOP_RIGHT);
        notificationPanel.setPadding(new Insets(10));
        notificationPanel.setPickOnBounds(false);
        StackPane.setAlignment(notificationPanel, Pos.TOP_RIGHT);

        chargerCategories();
        
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

        applyFiltersAndSort();
        updatePage();
        afficherNotificationsRejetees();
    }

    private void chargerCategories() {
        allCategories = new ArrayList<>();
        categoryController.loadCategories();
        List<Category> categories = categoryController.getCategories();
        System.out.println("📊 CategoryIndexView - Categories recues: " + (categories != null ? categories.size() : 0));
        
        if (categories != null && !categories.isEmpty()) {
            for (Category cat : categories) {
                String img = cat.getImageUrl();
                System.out.println("📦 Cat: " + cat.getName() + " | ImageUrl: '" + img + "'");
                int productCount = categoryController.getProductCount(cat.getId());
                double totalValue = productCount * 150000;
                allCategories.add(new CategoryDisplay(
                    cat,
                    img != null && !img.isEmpty() ? img : "/images/logo-icon2.png",
                    productCount,
                    totalValue,
                    String.format("%,.0f FCFA", totalValue)
                ));
            }
            System.out.println("✅ " + allCategories.size() + " categories chargees depuis la base");
        } else {
            System.out.println("⚠️ Aucune categorie trouvee, chargement des donnees par defaut");
            chargerCategoriesParDefaut();
        }
    }

    private void chargerCategoriesParDefaut() {
        allCategories = new ArrayList<>();
        allCategories.add(new CategoryDisplay(new Category(1, "Accessoire", "Cables, chargeurs, etuis", "approved"), "/images/product-1.png", 12, 125000, "15 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(2, "Ordinateur", "PC portables, fixes", "approved"), "/images/product-2.png", 8, 980000, "450 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(3, "Son et Audio", "Enceintes, ecouteurs", "pending"), "/images/product-3.png", 5, 210000, "85 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(4, "TV", "Televiseurs", "rejected"), "/images/product-4.png", 4, 450000, "320 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(5, "Telephone", "Smartphones", "approved"), "/images/product-5.png", 7, 680000, "220 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(6, "Gaming", "Consoles, jeux", "approved"), "/images/product-6.png", 3, 150000, "50 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(7, "Maison", "Electromenager", "pending"), "/images/product-7.png", 6, 320000, "45 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(8, "Sport", "Montres, equipements", "rejected"), "/images/product-8.png", 2, 98000, "25 000 FCFA"));
        allCategories.add(new CategoryDisplay(new Category(9, "Bureau", "Imprimantes, papeterie", "approved"), "/images/product-9.png", 4, 175000, "30 000 FCFA"));
    }

    // ... (createHeader, createFilterButton, createPagination, applyFiltersAndSort, updatePage, afficherCartes identiques)

    private VBox creerCarte(CategoryDisplay data) {
        Category cat = data.model;
        
        VBox carte = new VBox(8);
        carte.setAlignment(Pos.TOP_CENTER);
        carte.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 14; -fx-background-radius: 14; " +
                "-fx-background-color: white; -fx-padding: 10; " +
                "-fx-min-width: 210; -fx-min-height: 260; " +
                "-fx-max-width: 240; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");

        // ✅ IMAGE - CORRECTION FINALE
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(130);
        imageView.setPreserveRatio(true);
        
        String imgPath = data.imageUrl;
        System.out.println("🖼️ Tentative chargement: '" + imgPath + "'");
        
        boolean imageChargee = false;
        
        if (imgPath != null && !imgPath.isEmpty()) {
            // Essayer avec getClassLoader (sans le / initial)
            String path = imgPath.startsWith("/") ? imgPath.substring(1) : imgPath;
            System.out.println("   Essai 1 (ClassLoader): " + path);
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is != null) {
                imageView.setImage(new Image(is));
                imageChargee = true;
                System.out.println("   ✅ Succes ClassLoader");
            } else {
                // Essayer avec getResource (avec le / initial)
                String path2 = imgPath.startsWith("/") ? imgPath : "/" + imgPath;
                System.out.println("   Essai 2 (getResource): " + path2);
                is = getClass().getResourceAsStream(path2);
                if (is != null) {
                    imageView.setImage(new Image(is));
                    imageChargee = true;
                    System.out.println("   ✅ Succes getResource");
                }
            }
        }
        
        if (!imageChargee) {
            // Fallback: logo
            System.out.println("   ⚠️ Fallback: /images/logo-icon2.png");
            java.io.InputStream is = getClass().getResourceAsStream("/images/logo-icon2.png");
            if (is != null) {
                imageView.setImage(new Image(is));
                System.out.println("   ✅ Logo charge");
            } else {
                System.out.println("   ❌ Meme le logo est introuvable !");
            }
        }

        // Badge statut
        Label statusBadge = new Label();
        String status = cat.getStatus();
        if ("approved".equals(status)) {
            statusBadge.setText("✓ Validee");
            statusBadge.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 9px;");
        } else if ("pending".equals(status)) {
            statusBadge.setText("⏳ En attente");
            statusBadge.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 2 8; -fx-background-radius: 12; -fx-font-size: 9px;");
        } else {
            statusBadge.setText("✗ Rejetee");
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
        String categoryName = cat.getName();
        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "Categorie #" + cat.getId();
        }
        Label nomLabel = new Label(categoryName);
        nomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nomLabel.setWrapText(true);
        nomLabel.setAlignment(Pos.CENTER);
        nomLabel.setStyle("-fx-text-fill: #2d3748;");

        // Description
        Label descLabel = new Label(cat.getDescription() != null ? cat.getDescription() : "");
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

        // Boutons
        HBox buttonsBox = new HBox(6);
        buttonsBox.setAlignment(Pos.CENTER);

        Button voirBtn = new Button("Voir");
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");

        styleButton(voirBtn, "#28a745", "#20c997", 60);
        styleButton(modifierBtn, "#fd7e14", "#ffc107", 60);
        styleButton(supprimerBtn, "#dc3545", "#e74c3c", 60);

        int categoryId = cat.getId();
        voirBtn.setOnAction(e -> {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(categoryId));
            navigationManager.navigateToWithParams("categoryDetail", params);
        });
        modifierBtn.setOnAction(e -> {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(categoryId));
            navigationManager.navigateToWithParams("categoryEdit", params);
        });
        supprimerBtn.setOnAction(e -> {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(categoryId));
            navigationManager.navigateToWithParams("categoryDelete", params);
        });

        buttonsBox.getChildren().addAll(voirBtn, modifierBtn, supprimerBtn);

        carte.getChildren().addAll(imageStack, nomLabel, descLabel, priceLabel, statsBox, buttonsBox);
        return carte;
    }

    private void styleButton(Button btn, String color1, String color2, double prefWidth) {
        btn.setPrefWidth(prefWidth);
        btn.setPrefHeight(28);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + color1 + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 20; -fx-cursor: hand; -fx-border-color: #cccccc; -fx-border-radius: 20; -fx-border-width: 1.5;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + color1 + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 20; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-text-fill: " + color1 + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 20; -fx-cursor: hand; -fx-border-color: #cccccc; -fx-border-radius: 20; -fx-border-width: 1.5;"));
    }

    private void afficherNotificationsRejetees() {
        if (allCategories == null) return;
        List<CategoryDisplay> rejected = allCategories.stream().filter(d -> "rejected".equals(d.model.getStatus())).collect(Collectors.toList());
        if (rejected.isEmpty()) return;
        notificationPanel.getChildren().clear();
        for (CategoryDisplay data : rejected) notificationPanel.getChildren().add(createRejectedNotification(data));
    }

    private VBox createRejectedNotification(CategoryDisplay data) {
        VBox notif = new VBox(8);
        notif.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff9800; -fx-border-width: 0 0 0 4; -fx-background-radius: 8; -fx-padding: 12;");
        notif.setMaxWidth(360);
        HBox header = new HBox(10); header.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("⚠️"); icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("CATEGORIE REJETEE"); title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100; -fx-font-size: 12px;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("x"); closeBtn.setStyle("-fx-background: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> { FadeTransition ft = new FadeTransition(Duration.millis(200), notif); ft.setFromValue(1); ft.setToValue(0); ft.setOnFinished(ev -> notificationPanel.getChildren().remove(notif)); ft.play(); });
        header.getChildren().addAll(icon, title, spacer, closeBtn);
        Label message = new Label("La categorie \"" + data.model.getName() + "\" a ete rejetee.");
        message.setWrapText(true); message.setStyle("-fx-font-size: 12px;");
        Label reason = new Label("Raison : " + (data.model.getRejectionReason() != null ? data.model.getRejectionReason() : "Non specifiee"));
        reason.setStyle("-fx-font-size: 11px; -fx-text-fill: #bf360c; -fx-background-color: #fff8e1; -fx-padding: 6;");
        HBox actions = new HBox(10);
        Button editBtn = new Button("Modifier"); editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 4;");
        editBtn.setOnAction(e -> { Map<String, String> p = new HashMap<>(); p.put("id", String.valueOf(data.model.getId())); navigationManager.navigateToWithParams("categoryEdit", p); });
        Button deleteBtn = new Button("Supprimer"); deleteBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 4;");
        deleteBtn.setOnAction(e -> { Map<String, String> p = new HashMap<>(); p.put("id", String.valueOf(data.model.getId())); navigationManager.navigateToWithParams("categoryDelete", p); });
        actions.getChildren().addAll(editBtn, deleteBtn);
        notif.getChildren().addAll(header, message, reason, actions);
        return notif;
    }

    // ✅ MÉTHODES MANQUANTES (ajoutées)
    private VBox createHeader() {
        VBox headerBox = new VBox(15);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        HBox topRow = new HBox(15); topRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Categories"); title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        searchField = new TextField(); searchField.setPromptText("Rechercher une categorie..."); searchField.setPrefWidth(250); searchField.setStyle("-fx-padding: 8; -fx-background-radius: 20;");
        searchField.textProperty().addListener((obs, old, val) -> { currentPage = 0; applyFiltersAndSort(); updatePage(); });
        sortCombo = new ComboBox<>(); sortCombo.getItems().addAll("Nom (A-Z)", "Nom (Z-A)", "Produits (croissant)", "Produits (decroissant)", "Valeur (croissante)", "Valeur (decroissante)"); sortCombo.setValue("Nom (A-Z)");
        sortCombo.setOnAction(e -> { currentPage = 0; applyFiltersAndSort(); updatePage(); });
        addButton = new Button("+ Nouvelle categorie"); addButton.setStyle("-fx-background-color: #E66239; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 20; -fx-cursor: hand;");
        addButton.setOnAction(e -> navigationManager.navigateTo("categoryCreate"));
        topRow.getChildren().addAll(title, spacer, searchField, sortCombo, addButton);
        HBox filterRow = new HBox(10); filterRow.setAlignment(Pos.CENTER_LEFT); filterGroup = new ToggleGroup();
        ToggleButton allBtn = createFilterButton("Toutes", null); ToggleButton pendingBtn = createFilterButton("En attente", "pending"); ToggleButton approvedBtn = createFilterButton("Validees", "approved"); ToggleButton rejectedBtn = createFilterButton("Rejetees", "rejected"); allBtn.setSelected(true);
        filterRow.getChildren().addAll(allBtn, pendingBtn, approvedBtn, rejectedBtn);
        headerBox.getChildren().addAll(topRow, filterRow);
        return headerBox;
    }

    private ToggleButton createFilterButton(String text, String statusValue) {
        ToggleButton btn = new ToggleButton(text); btn.setToggleGroup(filterGroup); btn.setUserData(statusValue);
        btn.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #666; -fx-background-radius: 8; -fx-padding: 6 14;");
        btn.selectedProperty().addListener((obs, wasSel, isSel) -> { if (isSel) { currentPage = 0; applyFiltersAndSort(); updatePage(); } });
        return btn;
    }

    private HBox createPagination() {
        HBox paginationBox = new HBox(15); paginationBox.setAlignment(Pos.CENTER);
        prevBtn = new Button("◀ Precedent"); nextBtn = new Button("Suivant ▶"); pageInfo = new Label("Page 0 / 0");
        prevBtn.setOnAction(e -> { if (currentPage > 0) { currentPage--; updatePage(); } });
        nextBtn.setOnAction(e -> { if (sortedCategories != null && !sortedCategories.isEmpty()) { int maxPage = (int) Math.ceil((double) sortedCategories.size() / ITEMS_PER_PAGE) - 1; if (currentPage < maxPage) { currentPage++; updatePage(); } } });
        paginationBox.getChildren().addAll(prevBtn, pageInfo, nextBtn);
        return paginationBox;
    }

    private void applyFiltersAndSort() {
        if (allCategories == null) allCategories = new ArrayList<>();
        String search = searchField != null ? searchField.getText().toLowerCase() : "";
        String selectedStatus = null;
        ToggleButton selected = filterGroup != null ? (ToggleButton) filterGroup.getSelectedToggle() : null;
        if (selected != null && selected.getUserData() != null) selectedStatus = (String) selected.getUserData();
        final String finalSelectedStatus = selectedStatus;
        filteredCategories = new FilteredList<>(FXCollections.observableArrayList(allCategories), data -> {
            String name = data.model.getName() != null ? data.model.getName().toLowerCase() : "";
            String desc = data.model.getDescription() != null ? data.model.getDescription().toLowerCase() : "";
            return (name.contains(search) || desc.contains(search)) && (finalSelectedStatus == null || finalSelectedStatus.equals(data.model.getStatus()));
        });
        String sortType = sortCombo != null ? sortCombo.getValue() : "Nom (A-Z)";
        Comparator<CategoryDisplay> comparator = null;
        if ("Nom (A-Z)".equals(sortType)) comparator = Comparator.comparing((CategoryDisplay d) -> d.model.getName(), String.CASE_INSENSITIVE_ORDER);
        else if ("Nom (Z-A)".equals(sortType)) comparator = Comparator.comparing((CategoryDisplay d) -> d.model.getName(), String.CASE_INSENSITIVE_ORDER).reversed();
        else if ("Produits (croissant)".equals(sortType)) comparator = Comparator.comparingInt((CategoryDisplay d) -> d.productCount);
        else if ("Produits (decroissant)".equals(sortType)) comparator = Comparator.comparingInt((CategoryDisplay d) -> d.productCount).reversed();
        else if ("Valeur (croissante)".equals(sortType)) comparator = Comparator.comparingDouble((CategoryDisplay d) -> d.totalValue);
        else if ("Valeur (decroissante)".equals(sortType)) comparator = Comparator.comparingDouble((CategoryDisplay d) -> d.totalValue).reversed();
        sortedCategories = comparator != null ? new SortedList<>(filteredCategories, comparator) : new SortedList<>(filteredCategories);
    }

    private void updatePage() {
        if (sortedCategories == null || sortedCategories.isEmpty()) {
            cardsContainer.getChildren().clear();
            VBox emptyBox = new VBox(20); emptyBox.setAlignment(Pos.CENTER); emptyBox.setPadding(new Insets(50));
            Label emptyLabel = new Label("Aucune categorie disponible"); emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            emptyBox.getChildren().add(emptyLabel); cardsContainer.getChildren().add(emptyBox);
            pageInfo.setText("Page 0 / 0"); prevBtn.setDisable(true); nextBtn.setDisable(true);
            return;
        }
        int fromIndex = currentPage * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, sortedCategories.size());
        if (fromIndex >= sortedCategories.size()) { currentPage = Math.max(0, (int) Math.ceil((double) sortedCategories.size() / ITEMS_PER_PAGE) - 1); fromIndex = currentPage * ITEMS_PER_PAGE; toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, sortedCategories.size()); }
        afficherCartes(sortedCategories.subList(fromIndex, toIndex));
        int totalPages = Math.max(1, (int) Math.ceil((double) sortedCategories.size() / ITEMS_PER_PAGE));
        pageInfo.setText("Page " + (currentPage + 1) + " / " + totalPages);
        prevBtn.setDisable(currentPage == 0); nextBtn.setDisable(currentPage >= totalPages - 1);
    }

    private void afficherCartes(List<CategoryDisplay> categoriesPage) {
        cardsContainer.getChildren().clear();
        for (CategoryDisplay data : categoriesPage) {
            VBox carte = creerCarte(data);
            cardsContainer.getChildren().add(carte);
            FadeTransition ft = new FadeTransition(Duration.millis(350), carte); ft.setFromValue(0); ft.setToValue(1); ft.play();
        }
    }
}