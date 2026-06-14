package com.inapp;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import com.inapp.utils.NavigationManager;
import com.inapp.view.auth.Login;
import com.inapp.view.auth.Signin;
import com.inapp.view.auth.Signup;
import com.inapp.view.auth.Logout;
import com.inapp.view.admin.Dashboard;
import com.inapp.view.components.AdminSidebar;
import com.inapp.view.components.Footer;
import com.inapp.view.front.facture.FactureMainView;

import javafx.scene.layout.BorderPane;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationManager navigationManager = new NavigationManager(primaryStage);

        // Vues d'authentification
        navigationManager.registerView("login", new Login(navigationManager));
        navigationManager.registerView("signin", new Signin(navigationManager));
        navigationManager.registerView("signup", new Signup(navigationManager));
        navigationManager.registerView("logout", new Logout(navigationManager));

        // Vue admin (super admin)
        navigationManager.registerView("adminDashboard", createAdminLayout(navigationManager));

        // Vue front (utilisateur normal) – on la crée une fois et on la réutilise
        com.inapp.view.front.Dashboard frontDashboard = new com.inapp.view.front.Dashboard(navigationManager);
        navigationManager.registerView("frontDashboard", frontDashboard);

        // Tous les menus de la sidebar blanche pointent vers le même frontDashboard
        navigationManager.registerView("dashboard", frontDashboard);
        navigationManager.registerView("commandes", frontDashboard);
        // Enregistrer la vue facture
        FactureMainView factureMainView = new FactureMainView(navigationManager);
        navigationManager.registerView("factures", factureMainView.getView());
        navigationManager.registerView("categories", frontDashboard);
        navigationManager.registerView("products", frontDashboard);
        navigationManager.registerView("clients", frontDashboard);
        navigationManager.registerView("reports", frontDashboard);

        // Vues admin spécifiques (utilisées par la sidebar super admin)
        // On les garde mais on les redirige temporairement vers adminDashboard
        navigationManager.registerView("usersList", createAdminLayout(navigationManager));
        navigationManager.registerView("userAdd", createAdminLayout(navigationManager));
        navigationManager.registerView("categoryAdd", createAdminLayout(navigationManager));
        navigationManager.registerView("categoriesPending", createAdminLayout(navigationManager));
        navigationManager.registerView("productAdd", createAdminLayout(navigationManager));
        navigationManager.registerView("statistics", createAdminLayout(navigationManager));

        // Page de démarrage
        navigationManager.navigateTo("login");

        // Charger le CSS sur la scène
        Scene scene = primaryStage.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        }

        primaryStage.setTitle("PowerStock - Gestion Électroménager");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private BorderPane createAdminLayout(NavigationManager navManager) {
        BorderPane layout = new BorderPane();
        layout.setLeft(new AdminSidebar(navManager));
        layout.setCenter(new Dashboard());
        layout.setBottom(new Footer());
        return layout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}