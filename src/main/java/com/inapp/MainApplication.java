package com.inapp;

import javafx.application.Application;
import javafx.stage.Stage;
import com.inapp.view.front.Produit.ProductPage;

import com.inapp.utils.NavigationManager;
import com.inapp.view.auth.Login;
import com.inapp.view.auth.Signin;
import com.inapp.view.auth.Signup;
import com.inapp.view.auth.Logout;
import com.inapp.view.admin.Dashboard;
import com.inapp.view.components.AdminSidebar;
import com.inapp.view.components.Footer;
import com.inapp.view.front.Client.ClientsListView;
import com.inapp.view.front.Client.AddClientView;
import com.inapp.view.front.Client.EditClientView;
import com.inapp.view.front.Client.ViewClientView;
import javafx.scene.layout.BorderPane;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationManager navigationManager = new NavigationManager(primaryStage) {
            @Override
            public void navigateTo(String name) {
                // Vues client dynamiques (recréées à chaque navigation pour des données fraîches)
                if (name.equals("clients")) {
                    registerView(name, new ClientsListView(this));
                } else if (name.equals("clientAdd")) {
                    registerView(name, new AddClientView(this));
                } else if (name.startsWith("clientEdit?id=")) {
                    try {
                        int id = Integer.parseInt(name.split("=")[1]);
                        registerView(name, new EditClientView(this, id));
                    } catch (Exception ignored) {}
                } else if (name.startsWith("clientView?id=")) {
                    try {
                        int id = Integer.parseInt(name.split("=")[1]);
                        registerView(name, new ViewClientView(this, id));
                    } catch (Exception ignored) {}
                }
                super.navigateTo(name);
            }
        };

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

        /*Product pageProduit = new Product();
        navigationManager.registerView("productsList", pageProduit);*/

        ProductPage productPage = new ProductPage(navigationManager);
        navigationManager.registerView("products", productPage);


        navigationManager.registerView("dashboard", frontDashboard);
        navigationManager.registerView("commandes", frontDashboard);
        navigationManager.registerView("factures", frontDashboard);
        navigationManager.registerView("categories", frontDashboard);
<<<<<<< HEAD
        navigationManager.registerView("products", frontDashboard);
=======
        navigationManager.registerView("clients", frontDashboard);
>>>>>>> b94bf67c9cd214d3a644e0cb365068384555b880
        navigationManager.registerView("reports", frontDashboard);

        // Module Clients (notre partie)
        navigationManager.registerView("clients", new ClientsListView(navigationManager));
        navigationManager.registerView("clientAdd", new AddClientView(navigationManager));

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
