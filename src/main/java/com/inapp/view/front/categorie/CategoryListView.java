package com.inapp.view.front.categorie;

import com.inapp.model.Category;
import com.inapp.service.CategoryService;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Optional;

public class CategoryListView extends VBox {

    private final CategoryViewParent parent;
    private final CategoryService service = CategoryService.getInstance();
    private final FilteredList<Category> filteredCategories;
    private final FlowPane cardsPane = new FlowPane();
    private final Label resultCountLabel = new Label();
    private final TextField searchField = new TextField();
    private String activeFilter = "Toutes";

    public CategoryListView(CategoryViewParent parent) {
        this.parent = parent;
        this.filteredCategories = new FilteredList<>(service.getCategories(), category -> true);
        getChildren().add(createHeader());
        getChildren().add(createFilters());
        getChildren().add(createCardsPane());
        setPadding(new Insets(58, 32, 34, 32));
        setSpacing(20);
        setStyle("-fx-background-color: #fbfaf7;");
        applyFilter();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Gestion des Catégories");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: 400; -fx-text-fill: #2f3136;");
        Label subtitle = new Label("Gérez vos catégories de produits de manière efficace");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #5f5b57;");
        titleBox.getChildren().addAll(title, subtitle);

        Button addButton = new Button("+  Ajouter une catégorie");
        addButton.setStyle(CategoryViewSupport.primaryButtonStyle());
        addButton.setOnAction(event -> parent.showAdd());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, addButton);
        return header;
    }

    private VBox createFilters() {
        VBox filters = new VBox(12);

        HBox firstRow = new HBox(20);
        firstRow.setAlignment(Pos.CENTER_LEFT);
        searchField.setPromptText("Rechercher par nom ou description...");
        searchField.setPrefWidth(330);
        searchField.setMinHeight(36);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #e7e1d8 transparent; -fx-border-width: 0 0 1 0; -fx-padding: 7 10; -fx-font-size: 13px;");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        resultCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8f8982;");
        firstRow.getChildren().addAll(searchField, spacer, resultCountLabel);

        HBox tabRow = new HBox(10);
        tabRow.getChildren().addAll(
                createFilterButton("Toutes"),
                createFilterButton("En attente"),
                createFilterButton("Approuvée"),
                createFilterButton("Rejetée")
        );

        filters.getChildren().addAll(firstRow, tabRow);
        return filters;
    }

    private Button createFilterButton(String label) {
        Button button = new Button(label);
        button.setMinHeight(36);
        button.setStyle(filterButtonStyle(label.equals(activeFilter), label));
        button.setOnAction(event -> {
            activeFilter = label;
            HBox parentBox = (HBox) button.getParent();
            for (Node node : parentBox.getChildren()) {
                if (node instanceof Button) {
                    Button tab = (Button) node;
                    tab.setStyle(filterButtonStyle(tab.getText().equals(activeFilter), tab.getText()));
                }
            }
            applyFilter();
        });
        return button;
    }

    private String filterButtonStyle(boolean selected, String label) {
        if (selected) {
            return "-fx-background-color: #4b5563; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 8 18; -fx-background-radius: 5; -fx-cursor: hand;";
        }
        String color = "#e66239";
        if ("En attente".equals(label)) color = "#d99b18";
        if ("Approuvée".equals(label)) color = "#22a06b";
        if ("Rejetée".equals(label)) color = "#ef4444";
        return "-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-border-color: " + color + "55; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 13px; -fx-padding: 8 18; -fx-cursor: hand;";
    }

    private FlowPane createCardsPane() {
        cardsPane.setHgap(22);
        cardsPane.setVgap(20);
        cardsPane.setPadding(new Insets(6, 0, 20, 0));
        return cardsPane;
    }

    private void refreshCards() {
        cardsPane.getChildren().clear();
        for (Category category : filteredCategories) {
            cardsPane.getChildren().add(createCategoryCard(category));
        }
        resultCountLabel.setText(filteredCategories.size() + " catégories trouvées");
    }

    private VBox createCategoryCard(Category category) {
        VBox card = new VBox(0);
        card.setPrefWidth(208);
        card.setMinWidth(208);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #eee9df; -fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(26,20,12,0.035), 8, 0, 0, 1);");

        StackPane imageBox = CategoryViewSupport.categoryImage(category, 208, 92);
        imageBox.setStyle("-fx-background-color: #f4f0ea; -fx-background-radius: 5 5 0 0;");
        Label statusBadge = CategoryViewSupport.statusBadge(category);
        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(statusBadge, new Insets(10));
        imageBox.getChildren().add(statusBadge);

        VBox body = new VBox(8);
        body.setPadding(new Insets(12, 14, 12, 14));
        Label name = new Label(category.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #2f3136;");

        Label description = new Label(category.getDescription() == null || category.getDescription().isBlank() ? "Pas de description" : category.getDescription());
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");
        description.setWrapText(true);

        Button viewButton = CategoryViewSupport.iconButton("👁", "#e66239");
        viewButton.setOnAction(event -> parent.showDetails(category));
        Button editButton = CategoryViewSupport.iconButton("✎", "#d99b18");
        editButton.setOnAction(event -> parent.showEdit(category));
        Button deleteButton = CategoryViewSupport.iconButton("🗑", "#ef4444");
        deleteButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer " + category.getName() + " ?");
            alert.setContentText("Cette action est irréversible.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                service.deleteCategory(category);
                applyFilter();
            }
        });

        HBox actions = new HBox(8, viewButton, editButton, deleteButton);
        actions.setPadding(new Insets(34, 0, 0, 0));

        body.getChildren().addAll(name, description, actions);
        card.getChildren().addAll(imageBox, body);
        return card;
    }

    private void applyFilter() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        filteredCategories.setPredicate(category -> {
            boolean matchesQuery = query.isEmpty()
                    || category.getName().toLowerCase().contains(query)
                    || (category.getDescription() != null && category.getDescription().toLowerCase().contains(query));
            boolean matchesStatus = "Toutes".equals(activeFilter) || service.statusLabel(category.getStatus()).equals(activeFilter);
            return matchesQuery && matchesStatus;
        });
        refreshCards();
    }
}