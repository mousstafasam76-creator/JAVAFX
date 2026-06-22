package com.inapp.view.front.Category;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.inapp.utils.NavigationManager;
import com.inapp.model.Category;
import com.inapp.controller.front.CategoryController;

public class CategoryDeleteView extends VBox {

    private NavigationManager navigationManager;
    private int categoryId;
    private Category category;
    private int productCount;
    private TextField confirmField;
    private CheckBox deleteProductsCheck;
    private Button deleteBtn;
    private CategoryController categoryController;

    public CategoryDeleteView(NavigationManager navManager, int categoryId) {
        this.navigationManager = navManager;
        this.categoryId = categoryId;
        this.categoryController = CategoryController.getInstance();
        chargerCategorie();

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f5f7fa;");

        VBox container = new VBox(20);
        container.setMaxWidth(600);
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30;");

        Label title = new Label("⚠️ Suppression définitive");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #dc3545;");

        Label warning = new Label("Cette action est irréversible. La catégorie sera définitivement supprimée.");
        warning.setWrapText(true);

        VBox catBox = new VBox(10);
        catBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");
        Label catName = new Label(category.getName());
        catName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label catStatus = new Label("Statut : " + category.getStatus());
        catBox.getChildren().addAll(catName, catStatus);

        if (productCount > 0) {
            Label prodWarning = new Label("⚠️ Cette catégorie contient " + productCount + " produit(s).");
            prodWarning.setStyle("-fx-text-fill: #ffc107;");
            deleteProductsCheck = new CheckBox("Je confirme vouloir supprimer également les " + productCount + " produit(s) associés");
            catBox.getChildren().addAll(prodWarning, deleteProductsCheck);
        }

        Label confirmLabel = new Label("Tapez \"SUPPRIMER\" pour confirmer :");
        confirmField = new TextField();
        confirmField.setStyle("-fx-font-family: monospace; -fx-font-size: 18px; -fx-alignment: center;");
        confirmField.textProperty().addListener((obs, old, val) -> {
            deleteBtn.setDisable(!"SUPPRIMER".equals(val));
        });

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        deleteBtn = new Button("Supprimer définitivement");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 20;");
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(e -> deleteCategory());

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> navigationManager.navigateTo("categories"));

        buttons.getChildren().addAll(deleteBtn, cancelBtn);

        VBox conseqBox = new VBox(10);
        conseqBox.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-width: 0 0 0 4; -fx-padding: 15; -fx-background-radius: 8;");
        Label conseqTitle = new Label("Conséquences de la suppression :");
        conseqTitle.setStyle("-fx-font-weight: bold;");
        Label conseq1 = new Label("• La catégorie sera définitivement supprimée");
        Label conseq2 = new Label("• " + (productCount > 0 ? productCount + " produit(s) seront également supprimés (si coché)" : "Aucun produit associé"));
        Label conseq3 = new Label("• Cette action est irréversible");
        conseqBox.getChildren().addAll(conseqTitle, conseq1, conseq2, conseq3);

        container.getChildren().addAll(title, warning, catBox, confirmLabel, confirmField, buttons, conseqBox);
        getChildren().add(container);
    }

    private void chargerCategorie() {
        category = categoryController.findById(categoryId);
        if (category == null) {
            category = new Category(categoryId, "Catégorie inconnue", "", "unknown");
        }
        productCount = categoryController.getProductCount(categoryId);
    }

    private void deleteCategory() {
        if (deleteProductsCheck != null && !deleteProductsCheck.isSelected() && productCount > 0) {
            showAlert("Vous devez confirmer la suppression des produits associés.");
            return;
        }
        categoryController.deleteCategory(categoryId);
        showToast("Catégorie supprimée avec succès !");
        navigationManager.navigateTo("categories");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showToast(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}