package com.inapp.view.front;

import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Sidebar;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public abstract class FrontView extends BorderPane {

    protected NavigationManager navigationManager;

    public FrontView(NavigationManager navManager) {
        this.navigationManager = navManager;
        setLeft(new Sidebar(navManager));
        setCenter(createContent());
    }

    protected abstract Node createContent();
}