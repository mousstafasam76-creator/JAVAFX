package com.inapp;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import com.inapp.utils.NavigationManager;
import com.inapp.view.auth.Login;
import com.inapp.view.auth.Signin;
import com.inapp.view.auth.Signup;
import com.inapp.view.auth.Logout;
import com.inapp.view.admin.Dashboard;
import com.inapp.view.components.AdminSidebar;
import com.inapp.view.components.Footer;
import com.inapp.view.front.FrontDashboard;
import com.inapp.view.front.client.ClientsListView;
import com.inapp.view.front.client.AddClientView;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationManager nav = new NavigationManager(primaryStage);

        // Authentification
        nav.registerView("login", new Login(nav));
        nav.registerView("signin", new Signin(nav));
        nav.registerView("signup", new Signup(nav));
        nav.registerView("logout", new Logout(nav));

        // Vues front
        nav.registerView("dashboard", new FrontDashboard(nav));
        nav.registerView("clientsList", new ClientsListView(nav));
        nav.registerView("addClient", new AddClientView(nav));

        // Autres menus (provisoire)
        nav.registerView("commandes", new FrontDashboard(nav));
        nav.registerView("factures", new FrontDashboard(nav));
        nav.registerView("categoriesList", new FrontDashboard(nav));
        nav.registerView("productsList", new FrontDashboard(nav));
        nav.registerView("reports", new FrontDashboard(nav));

        // Vues admin
        nav.registerView("adminDashboard", createAdminLayout(nav));
        nav.registerView("usersList", createAdminLayout(nav));
        nav.registerView("userAdd", createAdminLayout(nav));
        nav.registerView("categoryAdd", createAdminLayout(nav));
        nav.registerView("categoriesPending", createAdminLayout(nav));
        nav.registerView("productAdd", createAdminLayout(nav));
        nav.registerView("statistics", createAdminLayout(nav));

        nav.navigateTo("login");

        primaryStage.setTitle("PowerStock - Gestion Électroménager");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private BorderPane createAdminLayout(NavigationManager nav) {
        BorderPane layout = new BorderPane();
        layout.setLeft(new AdminSidebar(nav));
        layout.setCenter(new Dashboard());
        layout.setBottom(new Footer());
        return layout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}