package com.inapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.inapp.utils.NavigationManager;
import com.inapp.view.auth.Login;
import com.inapp.view.auth.Signin;
import com.inapp.view.auth.Signup;
import com.inapp.view.auth.Logout;
import com.inapp.view.components.AdminSidebar;
import com.inapp.view.components.Footer;
import com.inapp.view.components.Sidebar;
import com.inapp.view.front.Dashboard;
import com.inapp.view.front.commande.CommandeListView;
import com.inapp.view.front.commande.Add;
import com.inapp.view.front.commande.Detail;
import com.inapp.view.front.commande.Edit;

public class MainApplication extends Application {

    private NavigationManager navigationManager;
    private BorderPane mainLayout;
    private BorderPane contentArea;
    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        navigationManager = new NavigationManager(primaryStage);
        
        // Créer la scène de connexion (sans sidebar)
        setupLoginScene();
        
        // Créer la scène principale (avec sidebar)
        setupMainScene();
        
        // Démarrer sur la scène de connexion
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("PowerStock - Connexion");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
    
    private void setupLoginScene() {
        // Page de connexion seule, sans layout
        Login loginView = new Login(navigationManager, this);
        loginScene = new Scene(loginView, 1400, 900);
        
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            loginScene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.out.println("CSS non trouvé: " + e.getMessage());
        }
    }
    
    private void setupMainScene() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f8f9fa;");
        
        Sidebar sidebar = new Sidebar(navigationManager);
        mainLayout.setLeft(sidebar);
        
        contentArea = new BorderPane();
        contentArea.setStyle("-fx-background-color: #f8f9fa;");
        mainLayout.setCenter(contentArea);
        
        // Enregistrer les vues qui seront affichées dans contentArea
        registerViews();
        
        // Définir le contentArea dans navigationManager
        navigationManager.setContentArea(contentArea);
        
        mainScene = new Scene(mainLayout, 1400, 900);
        
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            mainScene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.out.println("CSS non trouvé: " + e.getMessage());
        }
    }
    
    public void showMainScene() {
        // Afficher le dashboard après connexion
        navigationManager.navigateTo("dashboard");
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("PowerStock - Gestion Électroménager");
    }
    
    private void registerViews() {
        Dashboard frontDashboard = new Dashboard(navigationManager);
        
        CommandeListView commandeListView = new CommandeListView(navigationManager);
        navigationManager.registerView("commandes", commandeListView);
        navigationManager.registerView("commandesList", commandeListView);
        
        Add addCommandeView = new Add(navigationManager);
        navigationManager.registerView("commandeAdd", addCommandeView);
        
        Detail detailCommandeView = new Detail(navigationManager, 0);
        navigationManager.registerView("commandeDetails", detailCommandeView);
        
        Edit editCommandeView = new Edit(navigationManager, 0);
        navigationManager.registerView("commandeEdit", editCommandeView);
        
        navigationManager.registerView("dashboard", frontDashboard);
        navigationManager.registerView("frontDashboard", frontDashboard);
        navigationManager.registerView("factures", frontDashboard);
        navigationManager.registerView("categories", frontDashboard);
        navigationManager.registerView("products", frontDashboard);
        navigationManager.registerView("clients", frontDashboard);
        navigationManager.registerView("reports", frontDashboard);
        
        navigationManager.registerView("adminDashboard", createAdminLayout());
        navigationManager.registerView("usersList", createAdminLayout());
        navigationManager.registerView("userAdd", createAdminLayout());
        navigationManager.registerView("categoryAdd", createAdminLayout());
        navigationManager.registerView("categoriesPending", createAdminLayout());
        navigationManager.registerView("productAdd", createAdminLayout());
        navigationManager.registerView("statistics", createAdminLayout());
        
        // Vues d'authentification (enregistrées mais pas utilisées dans mainScene)
        navigationManager.registerView("signin", new Signin(navigationManager));
        navigationManager.registerView("signup", new Signup(navigationManager));
        navigationManager.registerView("logout", new Logout(navigationManager));
    }
    
    private BorderPane createAdminLayout() {
        BorderPane layout = new BorderPane();
        layout.setLeft(new AdminSidebar(navigationManager));
        layout.setCenter(new com.inapp.view.admin.Dashboard());
        layout.setBottom(new Footer());
        return layout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}