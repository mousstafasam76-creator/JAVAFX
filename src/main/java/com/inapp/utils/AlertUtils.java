package com.inapp.utils;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AlertUtils {
    
    private static Stage currentPopup;
    private static Timeline autoCloseTimer;
    
    public static void showSuccessMessage(String message) {
        showFloatingMessage(message, "#d4edda", "#155724", "✅", "success");
    }
    
    public static void showErrorMessage(String message) {
        showFloatingMessage(message, "#f8d7da", "#721c24", "❌", "error");
    }
    
    public static void showInfoMessage(String message) {
        showFloatingMessage(message, "#d1ecf1", "#0c5460", "ℹ️", "info");
    }
    
    public static void showWarningMessage(String message) {
        showFloatingMessage(message, "#fff3cd", "#856404", "⚠️", "warning");
    }
    
    private static void showFloatingMessage(String message, String bgColor, String textColor, String icon, String type) {
        if (currentPopup != null) {
            if (autoCloseTimer != null) autoCloseTimer.stop();
            currentPopup.close();
        }
        
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        
        VBox content = new VBox(8);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12px; -fx-padding: 15px 25px; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);
        
        content.getChildren().addAll(iconLabel, messageLabel);
        
        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: transparent;");
        
        javafx.scene.Scene scene = new javafx.scene.Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.setX(javafx.stage.Screen.getPrimary().getVisualBounds().getMaxX() - 350);
        popupStage.setY(20);
        
        // Animation d'entrée
        content.setOpacity(0);
        content.setScaleX(0.8);
        content.setScaleY(0.8);
        
        Timeline fadeIn = new Timeline(
            new KeyFrame(Duration.millis(200), new KeyValue(content.opacityProperty(), 1)),
            new KeyFrame(Duration.millis(200), new KeyValue(content.scaleXProperty(), 1)),
            new KeyFrame(Duration.millis(200), new KeyValue(content.scaleYProperty(), 1))
        );
        
        popupStage.show();
        fadeIn.play();
        
        currentPopup = popupStage;
        
        autoCloseTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(200), new KeyValue(content.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(200), new KeyValue(content.scaleXProperty(), 0.8)),
                new KeyFrame(Duration.millis(200), new KeyValue(content.scaleYProperty(), 0.8))
            );
            fadeOut.setOnFinished(e2 -> popupStage.close());
            fadeOut.play();
        }));
        autoCloseTimer.play();
    }
    
    public static boolean confirmDelete(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK;
    }
    
    public static boolean confirmDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK;
    }
}