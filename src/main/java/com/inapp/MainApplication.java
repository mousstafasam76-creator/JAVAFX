package com.inapp;

import javafx.application.Application;
import javafx.stage.Stage;
import com.inapp.utils.NavigationManager;
import com.inapp.view.auth.Login;
import com.inapp.view.auth.Signin;
import com.inapp.view.auth.Signup;
import com.inapp.view.auth.Logout;
import com.inapp.view.admin.Dashboard;
import com.inapp.view.components.AdminSidebar;
import com.inapp.view.components.Footer;
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

        // Vue front (utilisateur normal)
        com.inapp.view.front.Dashboard frontDashboard = new com.inapp.view.front.Dashboard(navigationManager);
        navigationManager.registerView("frontDashboard", frontDashboard);

        // Tous les menus de la sidebar blanche pointent vers le même frontDashboard
        navigationManager.registerView("dashboard", frontDashboard);
        navigationManager.registerView("commandes", frontDashboard);
        navigationManager.registerView("factures", frontDashboard);
        navigationManager.registerView("categories", frontDashboard);
        navigationManager.registerView("products", frontDashboard);
        navigationManager.registerView("clients", frontDashboard);
        navigationManager.registerView("reports", frontDashboard);

        // (Plus d'enregistrement pour "categoriesList")

        // Vues admin spécifiques
        navigationManager.registerView("usersList", createAdminLayout(navigationManager));
        navigationManager.registerView("userAdd", createAdminLayout(navigationManager));
        navigationManager.registerView("categoryAdd", createAdminLayout(navigationManager));
        navigationManager.registerView("categoriesPending", createAdminLayout(navigationManager));
        navigationManager.registerView("productAdd", createAdminLayout(navigationManager));
        navigationManager.registerView("statistics", createAdminLayout(navigationManager));

        navigationManager.navigateTo("login");

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