package com.inapp.view.front.categorie;

import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Sidebar;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class CategoryMainView extends BorderPane implements CategoryViewParent {

    private final NavigationManager navigationManager;

    public CategoryMainView(NavigationManager navigationManager) {
        this(navigationManager, "list");
    }

    public CategoryMainView(NavigationManager navigationManager, String startMode) {
        this.navigationManager = navigationManager;
        setStyle("-fx-background-color: #fbfaf7;");
        // Intégration de la sidebar
        setLeft(new Sidebar(navigationManager));
        // Pas de topBar pour éviter doublon
        if ("add".equals(startMode)) {
            showAdd();
        } else if ("pending".equals(startMode)) {
            showPending();
        } else {
            showList();
        }
    }

    @Override
    public void showList() {
        setContent(new CategoryListView(this));
    }

    @Override
    public void showAdd() {
        setContent(new CategoryAddView(this));
    }

    @Override
    public void showEdit(com.inapp.model.Category category) {
        setContent(new CategoryEditView(this, category));
    }

    @Override
    public void showDetails(com.inapp.model.Category category) {
        setContent(new CategoryDetailsView(this, category));
    }

    @Override
    public void showPending() {
        setContent(new CategoryPendingView(this));
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    private void setContent(Node node) {
        ScrollPane scrollPane = new ScrollPane(node);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #fbfaf7; -fx-background: #fbfaf7;");
        setCenter(scrollPane);
    }
}