package com.inapp.view.front.categorie;

import com.inapp.model.Category;
import com.inapp.service.CategoryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.File;

final class CategoryViewSupport {

    private CategoryViewSupport() {}

    static HBox topBar() {
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMinHeight(68);
        topBar.setPadding(new Insets(0, 24, 0, 18));
        topBar.setStyle("-fx-background-color: #fffdf9; -fx-border-color: #eee9df; -fx-border-width: 0 0 1 0;");

        Label toggle = new Label("▣");
        toggle.setStyle("-fx-text-fill: #77716a; -fx-font-size: 14px;");
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane notification = new StackPane();
        notification.setPrefSize(34, 34);
        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 17px;");
        Label badge = new Label("2");
        badge.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 1 5; -fx-background-radius: 10;");
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notification.getChildren().addAll(bell, badge);

        StackPane avatar = new StackPane();
        avatar.setPrefSize(38, 38);
        avatar.setStyle("-fx-background-color: #e66239; -fx-background-radius: 19;");
        Label initials = new Label("PA");
        initials.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        avatar.getChildren().add(initials);

        topBar.getChildren().addAll(toggle, spacer, notification, avatar);
        return topBar;
    }

    static HBox backHeader(String titleText, String subtitleText, Runnable onBack) {
        HBox header = new HBox(18);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("< Retour");
        backButton.setPrefSize(42, 38);
        backButton.setStyle(secondaryButtonStyle());
        backButton.setOnAction(event -> onBack.run());

        VBox titleBox = new VBox(4);
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 600; -fx-text-fill: #2f3136;");
        Label subtitle = new Label(subtitleText);
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(backButton, titleBox);
        return header;
    }

    static VBox panel() {
        VBox panel = new VBox(18);
        panel.setMaxWidth(620);
        panel.setPadding(new Insets(22));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 6; -fx-border-color: #eee9df; -fx-border-radius: 6;");
        return panel;
    }

    static Label statusBadge(Category category) {
        String status = category.getStatus();
        Label badge = new Label(CategoryService.getInstance().statusLabel(status));
        String color = "#f59e0b";
        if ("approved".equals(status)) color = "#22a06b";
        if ("rejected".equals(status)) color = "#ef4444";
        badge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: 700; -fx-padding: 4 9; -fx-background-radius: 12;");
        return badge;
    }

    static Button iconButton(String icon, String color) {
        Button button = new Button(icon);
        button.setPrefSize(48, 30);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-border-color: " + color + "55; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 12px; -fx-cursor: hand;");
        return button;
    }

    static StackPane categoryImage(Category category, double width, double height) {
        StackPane imageBox = new StackPane();
        imageBox.setPrefSize(width, height);
        imageBox.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 7;");

        Image image = loadImage(category.getImageUrl());
        if (image != null && !image.isError()) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(false);
            imageBox.getChildren().add(imageView);
        } else {
            Label fallback = new Label(category.getName());
            fallback.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");
            imageBox.getChildren().add(fallback);
        }
        return imageBox;
    }

    static VBox formGroup(String labelText, Node field) {
        VBox group = new VBox(7);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        group.getChildren().addAll(label, field);
        return group;
    }

    static String primaryButtonStyle() {
        return "-fx-background-color: #e66239; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 11 18; -fx-background-radius: 4; -fx-cursor: hand;";
    }

    static String secondaryButtonStyle() {
        return "-fx-background-color: white; -fx-text-fill: #4b5563; -fx-border-color: #d1d5db; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 10 16; -fx-cursor: hand;";
    }

    static String inputStyle() {
        return "-fx-background-color: white; -fx-border-color: #eee9df; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 9 12; -fx-font-size: 13px;";
    }

    private static Image loadImage(String path) {
        if (path == null || path.isBlank()) return null;
        // Essayer chemin système (fichier local)
        File file = new File(path);
        if (file.exists()) {
            return new Image(file.toURI().toString());
        }
        // Sinon ressource interne
        java.io.InputStream stream = CategoryViewSupport.class.getResourceAsStream(path);
        if (stream != null) {
            return new Image(stream);
        }
        return null;
    }
}