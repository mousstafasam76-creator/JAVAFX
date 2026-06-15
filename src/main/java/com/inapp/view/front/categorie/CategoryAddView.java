package com.inapp.view.front.categorie;

import com.inapp.service.CategoryService;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.File;

public class CategoryAddView extends VBox {

    private final CategoryViewParent parent;
    private final CategoryService service = CategoryService.getInstance();

    public CategoryAddView(CategoryViewParent parent) {
        this.parent = parent;
        getChildren().add(CategoryViewSupport.backHeader(
                "Ajouter une catégorie",
                "Remplissez le formulaire pour créer une catégorie",
                parent::showList
        ));
        getChildren().add(createForm());
        setSpacing(22);
        setPadding(new javafx.geometry.Insets(58, 32, 34, 32));
        setStyle("-fx-background-color: #fbfaf7;");
    }

    private VBox createForm() {
        VBox form = CategoryViewSupport.panel();

        TextField nameField = new TextField();
        nameField.setPromptText("Nom de la catégorie");
        nameField.setStyle(CategoryViewSupport.inputStyle());

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(4);
        descriptionField.setWrapText(true);
        descriptionField.setStyle(CategoryViewSupport.inputStyle());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("En attente", "Approuvée", "Rejetée");
        statusBox.setValue("En attente");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setStyle(CategoryViewSupport.inputStyle());

        TextField imageField = new TextField("/images/products/product-1.png");
        imageField.setPromptText("/images/products/product-1.png");
        imageField.setStyle(CategoryViewSupport.inputStyle());

        Button browseImageButton = new Button("Parcourir");
        browseImageButton.setStyle(CategoryViewSupport.secondaryButtonStyle());
        browseImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = fileChooser.showOpenDialog(getScene() != null ? getScene().getWindow() : null);
            if (file != null) {
                imageField.setText(file.toURI().toString());
            }
        });

        HBox imageFieldRow = new HBox(8, imageField, browseImageButton);
        imageFieldRow.setFillHeight(true);
        HBox.setHgrow(imageField, javafx.scene.layout.Priority.ALWAYS);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");

        Button saveButton = new Button("Ajouter la catégorie");
        saveButton.setStyle(CategoryViewSupport.primaryButtonStyle());
        saveButton.setOnAction(event -> {
            String name = value(nameField);
            if (name.isEmpty()) {
                errorLabel.setText("Le nom de la catégorie est obligatoire.");
                return;
            }
            service.addCategory(
                    name,
                    value(descriptionField),
                    service.statusValue(statusBox.getValue()),
                    value(imageField)
            );
            parent.showList();
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle(CategoryViewSupport.secondaryButtonStyle());
        cancelButton.setOnAction(event -> parent.showList());

        HBox actions = new HBox(12, saveButton, cancelButton);
        form.getChildren().addAll(
                CategoryViewSupport.formGroup("Nom", nameField),
                CategoryViewSupport.formGroup("Description", descriptionField),
                CategoryViewSupport.formGroup("Statut", statusBox),
                CategoryViewSupport.formGroup("Image", imageFieldRow),
                errorLabel,
                actions
        );
        return form;
    }

    private String value(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String value(TextArea field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}