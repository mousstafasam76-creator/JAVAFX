package com.inapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.inapp.view.front.Produit.ProductPage;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.SessionManager;
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
import com.inapp.view.front.facture.FactureMainView;
import com.inapp.view.front.Category.CategoryIndexView;
import com.inapp.view.front.Category.CategoryCreateView;
import com.inapp.view.front.Category.CategoryDetailView;
import com.inapp.view.front.Category.CategoryEditView;
import com.inapp.view.front.Category.CategoryDeleteView;

public class MainApplication extends Application {

    private NavigationManager navigationManager;
    private Stage primaryStage;
    private Scene loginScene;
    private Scene userScene;    // Scene pour utilisateur normal
    private Scene adminScene;   // Scene pour administrateur

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== DEMARRAGE DE L'APPLICATION ===");
        this.primaryStage = primaryStage;
        
        // Stocker la reference pour le Logout
        primaryStage.getProperties().put("mainApp", this);
        
        this.navigationManager = new NavigationManager(primaryStage);
        System.out.println("NavigationManager cree");
        
        setupLoginScene();
        System.out.println("LoginScene configuree");
        
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("PowerStock - Connexion");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        System.out.println("Fenetre affichee");
    }
    
    private void setupLoginScene() {
        try {
            Login loginView = new Login(navigationManager, this);
            loginScene = new Scene(loginView, 1200, 700);
            System.out.println("LoginView creee");
        } catch (Exception e) {
            System.out.println("Erreur creation login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void showLoginScene() {
        System.out.println("=== RETOUR A LA PAGE DE CONNEXION ===");
        SessionManager.getInstance().clearSession();
        navigationManager.clearAllViews();
        setupLoginScene();
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("PowerStock - Connexion");
        primaryStage.show();
        System.out.println("Page de connexion affichee");
    }
    
    /**
     * Affiche la scene appropriee selon le role de l'utilisateur
     */
    public void showMainScene() {
        System.out.println("=== PASSAGE A LA SCENE PRINCIPALE ===");
        
        String role = SessionManager.getInstance().getCurrentUserRole();
        System.out.println("Role utilisateur: " + role);
        
        if ("admin".equals(role) || "super_admin".equals(role)) {
            // ADMIN : creer la scene admin si pas encore faite
            if (adminScene == null) {
                setupAdminScene();
            }
            navigationManager.navigateTo("adminDashboard");
            primaryStage.setScene(adminScene);
            primaryStage.setTitle("PowerStock - Administration");
        } else {
            // USER : creer la scene user si pas encore faite
            if (userScene == null) {
                setupUserScene();
            }
            navigationManager.navigateTo("dashboard");
            primaryStage.setScene(userScene);
            primaryStage.setTitle("PowerStock - Gestion");
        }
        primaryStage.show();
    }
    
    private void setupUserScene() {
        try {
            BorderPane userLayout = new BorderPane();
            userLayout.setStyle("-fx-background-color: #f8f9fa;");
            
            // Sidebar normale pour utilisateur
            Sidebar sidebar = new Sidebar(navigationManager);
            userLayout.setLeft(sidebar);
            
            // Footer
            userLayout.setBottom(new Footer());
            
            // ContentArea
            BorderPane contentArea = new BorderPane();
            contentArea.setStyle("-fx-background-color: #f8f9fa;");
            userLayout.setCenter(contentArea);
            
            navigationManager.setContentArea(contentArea);
            
            // Enregistrer les vues utilisateur
            registerUserViews();
            
            userScene = new Scene(userLayout, 1200, 700);
            
            try {
                String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
                if (cssPath != null) userScene.getStylesheets().add(cssPath);
            } catch (Exception e) {}
            
            System.out.println("UserScene configuree");
        } catch (Exception e) {
            System.out.println("Erreur creation userScene: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupAdminScene() {
        try {
            BorderPane adminLayout = new BorderPane();
            adminLayout.setStyle("-fx-background-color: #f8f9fa;");
            
            // AdminSidebar pour administrateur
            AdminSidebar adminSidebar = new AdminSidebar(navigationManager);
            adminLayout.setLeft(adminSidebar);
            
            // Footer
            adminLayout.setBottom(new Footer());
            
            // ContentArea
            BorderPane contentArea = new BorderPane();
            contentArea.setStyle("-fx-background-color: #f8f9fa;");
            adminLayout.setCenter(contentArea);
            
            // Dashboard admin
            contentArea.setCenter(new com.inapp.view.admin.Dashboard());
            
            navigationManager.setContentArea(contentArea);
            
            // Enregistrer les vues admin
            registerAdminViews();
            
            adminScene = new Scene(adminLayout, 1200, 700);
            
            try {
                String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
                if (cssPath != null) adminScene.getStylesheets().add(cssPath);
            } catch (Exception e) {}
            
            System.out.println("AdminScene configuree");
        } catch (Exception e) {
            System.out.println("Erreur creation adminScene: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void registerUserViews() {
        try {
            Dashboard frontDashboard = new Dashboard(navigationManager);
            
            // Commandes
            CommandeListView commandeListView = new CommandeListView(navigationManager);
            navigationManager.registerProtectedContent("commandes", commandeListView);
            navigationManager.registerProtectedContent("commandesList", commandeListView);
            
            Add addCommandeView = new Add(navigationManager);
            navigationManager.registerProtectedContent("commandeAdd", addCommandeView);
            
            // Produits
            ProductPage productPage = new ProductPage(navigationManager);
            navigationManager.registerProtectedContent("products", productPage);
            navigationManager.registerProtectedContent("productsList", productPage);
            
            // Clients
            ClientsListView clientsListView = new ClientsListView(navigationManager);
            navigationManager.registerProtectedContent("clients", clientsListView);
            navigationManager.registerProtectedContent("clientsList", clientsListView);
            
            AddClientView addClientView = new AddClientView(navigationManager);
            navigationManager.registerProtectedContent("clientAdd", addClientView);
            
            // Factures
            FactureMainView factureMainView = new FactureMainView(navigationManager);
            navigationManager.registerProtectedContent("factures", factureMainView.getView());
            
            // Categories
            CategoryIndexView categoryIndexView = new CategoryIndexView(navigationManager);
            navigationManager.registerProtectedContent("categories", categoryIndexView);
            navigationManager.registerProtectedContent("categoriesList", categoryIndexView);
            
            CategoryCreateView categoryCreateView = new CategoryCreateView(navigationManager);
            navigationManager.registerProtectedContent("categoryCreate", categoryCreateView);
            
            CategoryDetailView categoryDetailView = new CategoryDetailView(navigationManager, 0);
            navigationManager.registerProtectedContent("categoryDetail", categoryDetailView);
            
            CategoryEditView categoryEditView = new CategoryEditView(navigationManager, 0);
            navigationManager.registerProtectedContent("categoryEdit", categoryEditView);
            
            CategoryDeleteView categoryDeleteView = new CategoryDeleteView(navigationManager, 0);
            navigationManager.registerProtectedContent("categoryDelete", categoryDeleteView);
            
            // Dashboard
            navigationManager.registerProtectedContent("dashboard", frontDashboard);
            navigationManager.registerProtectedContent("frontDashboard", frontDashboard);
            navigationManager.registerProtectedContent("reports", frontDashboard);
            
            // Auth
            navigationManager.registerView("signin", new Signin(navigationManager));
            navigationManager.registerView("signup", new Signup(navigationManager));
            navigationManager.registerView("logout", new Logout(navigationManager));
            
            System.out.println("Vues utilisateur enregistrees");
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
    
    private void registerAdminViews() {
        try {
            // Dashboard admin
            com.inapp.view.admin.Dashboard adminDashboard = new com.inapp.view.admin.Dashboard();
            navigationManager.registerProtectedContent("adminDashboard", adminDashboard);
            
            // Gestion des utilisateurs
            navigationManager.registerProtectedContent("usersList", adminDashboard);
            navigationManager.registerProtectedContent("userAdd", adminDashboard);
            
            // Categories admin
            navigationManager.registerProtectedContent("categoryAdd", adminDashboard);
            navigationManager.registerProtectedContent("categoriesPending", adminDashboard);
            
            // Produits admin
            navigationManager.registerProtectedContent("productAdd", adminDashboard);
            
            // Statistiques
            navigationManager.registerProtectedContent("statistics", adminDashboard);
            
            // Auth
            navigationManager.registerView("logout", new Logout(navigationManager));
            
            System.out.println("Vues admin enregistrees");
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
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
