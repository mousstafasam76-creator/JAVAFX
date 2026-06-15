package com.inapp.view.front.categorie;

import com.inapp.model.Category;
import com.inapp.service.CategoryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CategoryPendingView extends VBox {

    private final CategoryViewParent parent;
    private final CategoryService service = CategoryService.getInstance();
    private final VBox list = new VBox(12);

    public CategoryPendingView(CategoryViewParent parent) {
        this.parent = parent;
        getChildren().add(CategoryViewSupport.backHeader(
                "Catégories en attente",
                "Validez ou modifiez les catégories qui attendent une décision",
                parent::showList
        ));
        getChildren().add(list);
        setSpacing(22);
        setPadding(new Insets(58, 32, 34, 32));
        setStyle("-fx-background-color: #fbfaf7;");
        refresh();
    }

    private void refresh() {
        list.getChildren().clear();
        for (Category category : service.getPendingCategories()) {
            list.getChildren().add(row(category));
        }
        if (list.getChildren().isEmpty()) {
            Label empty = new Label("Aucune catégorie en attente");
            empty.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
            list.getChildren().add(empty);
        }
    }

    private HBox row(Category category) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 7; -fx-border-color: #edf0f3; -fx-border-radius: 7;");

        VBox text = new VBox(4);
        Label name = new Label(category.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #2f3136;");
        Label description = new Label(category.getDescription() == null || category.getDescription().isBlank() ? "Pas de description" : category.getDescription());
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");
        text.getChildren().addAll(name, description);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button approve = new Button("Approuver");
        approve.setStyle(CategoryViewSupport.primaryButtonStyle());
        approve.setOnAction(event -> {
            service.updateCategory(category, category.getName(), category.getDescription(), "approved", category.getImageUrl());
            refresh();
        });

        Button edit = new Button("Modifier");
        edit.setStyle(CategoryViewSupport.secondaryButtonStyle());
        edit.setOnAction(event -> parent.showEdit(category));

        row.getChildren().addAll(text, spacer, approve, edit);
        return row;
    }
}