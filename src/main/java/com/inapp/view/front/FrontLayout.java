package com.inapp.view.front;

import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Sidebar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class FrontLayout extends BorderPane {

    private final NavigationManager navigationManager;
    private final StackPane centerContainer;

    public FrontLayout(NavigationManager navManager) {
        this.navigationManager = navManager;
        this.centerContainer = new StackPane();

        // Ajout de la sidebar (blanche) à gauche
        Sidebar sidebar = new Sidebar(navManager);
        setLeft(sidebar);
        setCenter(centerContainer);

        // Optionnel : enregistrer les vues front dans le centre
        // (mais cela se fait déjà dans MainApplication)
    }

    /**
     * Affiche une vue dans la zone centrale.
     */
    public void showView(javafx.scene.Parent view) {
        centerContainer.getChildren().setAll(view);
    }
}