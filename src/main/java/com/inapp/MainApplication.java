package com.inapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.inapp.view.front.Produit.ProductPage;
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
import com.inapp.view.front.Client.ClientsListView;
import com.inapp.view.front.Client.AddClientView;
import com.inapp.view.front.Client.EditClientView;
import com.inapp.view.front.Client.ViewClientView;

public class MainApplication extends Application {

    private NavigationManager navigationManager;
    private BorderPane mainLayout;
    private BorderPane contentArea;
    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== DÉMARRAGE DE L'APPLICATION ===");
        this.primaryStage = primaryStage;
        this.navigationManager = new NavigationManager(primaryStage);
        System.out.println("NavigationManager créé");
        
        setupLoginScene();
        System.out.println("LoginScene configurée");
        
        setupMainScene();
        System.out.println("MainScene configurée");
        
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("PowerStock - Connexion");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        System.out.println("Fenêtre affichée");
    }
    
    private void setupLoginScene() {
        try {
            Login loginView = new Login(navigationManager, this);
            loginScene = new Scene(loginView, 1400, 900);
            System.out.println("LoginView créée");
        } catch (Exception e) {
            System.out.println("Erreur création login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupMainScene() {
        try {
            mainLayout = new BorderPane();
            mainLayout.setStyle("-fx-background-color: #f8f9fa;");
            
            Sidebar sidebar = new Sidebar(navigationManager);
            mainLayout.setLeft(sidebar);
            System.out.println("Sidebar ajouté");
            
            contentArea = new BorderPane();
            contentArea.setStyle("-fx-background-color: #f8f9fa;");
            mainLayout.setCenter(contentArea);
            System.out.println("ContentArea créé");
            
            registerViews();
            System.out.println("Vues enregistrées");
            
            navigationManager.setContentArea(contentArea);
            
            mainScene = new Scene(mainLayout, 1400, 900);
            
            try {
                String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
                if (cssPath != null) {
                    mainScene.getStylesheets().add(cssPath);
                    System.out.println("CSS chargé pour main");
                }
            } catch (Exception e) {
                System.out.println("CSS non trouvé pour main: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Erreur création mainScene: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void showMainScene() {
        System.out.println("=== PASSAGE À LA SCÈNE PRINCIPALE ===");
        navigationManager.navigateTo("dashboard");
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("PowerStock - Gestion Électroménager");
        primaryStage.show();
    }
    
    private void registerViews() {
        try {
            Dashboard frontDashboard = new Dashboard(navigationManager);
            
            // ========== VUES COMMANDES ==========
            CommandeListView commandeListView = new CommandeListView(navigationManager);
            navigationManager.registerProtectedContent("commandes", commandeListView);
            navigationManager.registerProtectedContent("commandesList", commandeListView);
            System.out.println("CommandeListView enregistrée");
            
            Add addCommandeView = new Add(navigationManager);
            navigationManager.registerProtectedContent("commandeAdd", addCommandeView);
            System.out.println("Add commande enregistrée");
            
            // ========== VUES PRODUITS ==========
            ProductPage productPage = new ProductPage(navigationManager);
            navigationManager.registerProtectedContent("products", productPage);
            navigationManager.registerProtectedContent("productsList", productPage);
            System.out.println("ProductPage enregistrée");
            
            // ========== VUES CLIENTS ==========
            ClientsListView clientsListView = new ClientsListView(navigationManager);
            navigationManager.registerProtectedContent("clients", clientsListView);
            navigationManager.registerProtectedContent("clientsList", clientsListView);
            System.out.println("ClientsListView enregistrée");
            
            AddClientView addClientView = new AddClientView(navigationManager);
            navigationManager.registerProtectedContent("clientAdd", addClientView);
            System.out.println("AddClientView enregistrée");
            
            // ========== VUES PRINCIPALES ==========
            navigationManager.registerProtectedContent("dashboard", frontDashboard);
            navigationManager.registerProtectedContent("frontDashboard", frontDashboard);
            navigationManager.registerProtectedContent("factures", frontDashboard);
            navigationManager.registerProtectedContent("categories", frontDashboard);
            navigationManager.registerProtectedContent("categoriesList", frontDashboard);
            navigationManager.registerProtectedContent("reports", frontDashboard);
            
            // ========== VUES ADMIN (layout différent) ==========
            BorderPane adminLayout = new BorderPane();
            adminLayout.setLeft(new AdminSidebar(navigationManager));
            adminLayout.setCenter(new com.inapp.view.admin.Dashboard());
            adminLayout.setBottom(new Footer());
            
            navigationManager.registerView("adminDashboard", adminLayout);
            navigationManager.registerView("usersList", adminLayout);
            navigationManager.registerView("userAdd", adminLayout);
            navigationManager.registerView("categoryAdd", adminLayout);
            navigationManager.registerView("categoriesPending", adminLayout);
            navigationManager.registerView("productAdd", adminLayout);
            navigationManager.registerView("statistics", adminLayout);
            
            // ========== VUES D'AUTHENTIFICATION ==========
            navigationManager.registerView("signin", new Signin(navigationManager));
            navigationManager.registerView("signup", new Signup(navigationManager));
            navigationManager.registerView("logout", new Logout(navigationManager));
            
            System.out.println("Toutes les vues enregistrées");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'enregistrement des vues: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        System.out.println("=== LANCEMENT DE L'APPLICATION ===");
        launch(args);
    }
}