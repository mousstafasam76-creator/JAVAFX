package com.inapp.view.front.categorie;

import com.inapp.service.CategoryService;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CategoryDetailsView extends VBox {

    private final CategoryViewParent parent;
    private final com.inapp.model.Category category;
    private final CategoryService service = CategoryService.getInstance();

    public CategoryDetailsView(CategoryViewParent parent, com.inapp.model.Category category) {
        this.parent = parent;
        this.category = category;
        getChildren().add(CategoryViewSupport.backHeader(
                "Détail de la catégorie",
                "Consultez les informations de " + category.getName(),
                parent::showList
        ));
        getChildren().add(createDetails());
        getChildren().add(createActions());
        setSpacing(22);
        setPadding(new javafx.geometry.Insets(58, 32, 34, 32));
        setStyle("-fx-background-color: #fbfaf7;");
    }

    private VBox createDetails() {
        VBox card = CategoryViewSupport.panel();
        StackPane image = CategoryViewSupport.categoryImage(category, 420, 190);
        image.setMaxWidth(420);
        card.getChildren().addAll(
                image,
                row("Nom", category.getName()),
                row("Description", emptyText(category.getDescription())),
                row("Statut", service.statusLabel(category.getStatus())),
                row("Image", emptyText(category.getImageUrl()))
        );
        return card;
    }

    private HBox createActions() {
        Button editButton = new Button("Modifier");
        editButton.setStyle(CategoryViewSupport.primaryButtonStyle());
        editButton.setOnAction(event -> parent.showEdit(category));

        Button backButton = new Button("Retour");
        backButton.setStyle(CategoryViewSupport.secondaryButtonStyle());
        backButton.setOnAction(event -> parent.showList());
        return new HBox(12, editButton, backButton);
    }

    private HBox row(String labelText, String valueText) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.TOP_LEFT);
        Label label = new Label(labelText);
        label.setMinWidth(120);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        Label value = new Label(valueText);
        value.setWrapText(true);
        value.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");
        row.getChildren().addAll(label, value);
        return row;
    }

    private String emptyText(String value) {
        return value == null || value.isBlank() ? "Pas de description" : value;
    }
}