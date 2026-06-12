package com.inapp.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Footer extends HBox {
    
    public Footer() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 1px 0 0 0;");
        
        Label copyright = new Label("© 2024 InApp - Tous droits réservés");
        copyright.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        getChildren().add(copyright);
    }
}