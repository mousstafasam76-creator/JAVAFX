package com.inapp.view.front;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.inapp.utils.NavigationManager;
import com.inapp.utils.SessionManager;
import com.inapp.view.components.Sidebar;
import com.inapp.view.components.Footer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Dashboard extends BorderPane {

    private NavigationManager navManager;
    private Label clockLabel;
    private Timeline clockTimeline;

    // Données fictives (comme le PHP avec la BD déconnectée)
    private final String totalSales      = "450,000 FCFA";
    private final String totalCommandes  = "1,234";
    private final String totalExpenses   = "85,000 FCFA";
    private final String totalProfit     = "365,000 FCFA";
    private final String totalProduits   = "1,890";
    private final String totalClients    = "567";
    private final int    firstTime       = 255;
    private final int    returnCustomers = 312;
    private final int    totalCustomers  = 567;

    public Dashboard(NavigationManager navManager) {
        this.navManager = navManager;
        setupUI();
        startClock();
    }

    private void setupUI() {
        setLeft(new Sidebar(navManager));
        setBottom(new Footer());

        // ========== CONTENU CENTRAL ==========
        VBox content = new VBox(25);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #f5f7fa;");

        // 1. Titre + bienvenue
        content.getChildren().add(createTitleSection());

        // 2. Les 4 cartes statistiques colorées
        content.getChildren().add(createStatsCards());

        // 3. Les 3 cartes supplémentaires
        content.getChildren().add(createExtraCards());

        // 4. Graphiques (ventes mensuelles + aperçu clients)
        content.getChildren().add(createChartsRow());

        // 5. Listes (top produits, stock faible, dernières ventes)
        content.getChildren().add(createListsRow());

        // ScrollPane pour toujours commencer en haut
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");
        setCenter(scrollPane);
    }

    // ==================== 1. TITRE ====================
    private VBox createTitleSection() {
        VBox box = new VBox(5);
        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        String userName = SessionManager.getInstance().getCurrentUserFullName();
        if (userName.isEmpty()) userName = "Utilisateur";
        Label subtitle = new Label("Bienvenue, " + userName + " !");
        subtitle.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
        box.getChildren().addAll(title, subtitle);
        return box;
    }

    // ==================== 2. CARTES STATISTIQUES ====================
    private GridPane createStatsCards() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // Mêmes données et couleurs que le PHP
        String[][] cards = {
            {"📈", "Chiffre d'affaires", totalSales,    "Total des ventes",   "#E66239", "rgba(230,98,57,0.1)"},
            {"🔄", "Total commandes",   totalCommandes, "Commandes passées",   "#10b981", "rgba(16,185,129,0.1)"},
            {"💳", "Factures impayées", totalExpenses,  "À encaisser",         "#3b82f6", "rgba(59,130,246,0.1)"},
            {"💰", "Bénéfice net",      totalProfit,    "CA - Impayés",        "#f59e0b", "rgba(245,158,11,0.1)"}
        };

        for (int i = 0; i < cards.length; i++) {
            VBox card = createColoredStatCard(
                cards[i][0], cards[i][1], cards[i][2],
                cards[i][3], cards[i][4], cards[i][5]
            );
            grid.add(card, i, 0);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }
        return grid;
    }

    private VBox createColoredStatCard(String icon, String label, String value,
                                        String description, String color, String bgColor) {
        VBox card = new VBox(0);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 12px;"
        );

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        // Icône dans un carré coloré (comme icon-shape en PHP)
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(48, 48);
        iconBox.setMinSize(48, 48);
        iconBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8px;");
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white;");
        iconBox.getChildren().add(iconLabel);

        Label labelTitle = new Label(label);
        labelTitle.setStyle("-fx-text-fill: #333; -fx-font-size: 13px; -fx-font-weight: bold;");

        header.getChildren().addAll(iconBox, labelTitle);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");
        VBox.setMargin(valueLabel, new Insets(12, 0, 4, 0));

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");

        card.getChildren().addAll(header, valueLabel, descLabel);
        return card;
    }

    // ==================== 3. CARTES SUPPLÉMENTAIRES ====================
    private HBox createExtraCards() {
        HBox row = new HBox(20);

        row.getChildren().addAll(
            createExtraCard("💰", "Total Profit", totalSales, "Ventes totales", "#667eea", "Voir"),
            createExtraCard("💳", "Total à encaisser", totalExpenses, "Factures impayées", "#dc3545", "Voir"),
            createExtraCard("🏦", "Bénéfice net", totalProfit, "Après impayés", "#f59e0b", "Détails")
        );
        for (int i = 0; i < 3; i++) HBox.setHgrow(row.getChildren().get(i), Priority.ALWAYS);
        return row;
    }

    private VBox createExtraCard(String icon, String title, String value,
                                  String footer, String color, String link) {
        VBox card = new VBox(0);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        // En-tête avec valeur et icône
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        VBox left = new VBox(5);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        left.getChildren().addAll(valueLabel, titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: " + color + ";");

        top.getChildren().addAll(left, spacer, iconLabel);

        // Séparateur
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e0e0e0;");
        VBox.setMargin(sep, new Insets(15, 0, 10, 0));

        // Footer
        HBox bottom = new HBox(5);
        Label footerLabel = new Label(footer);
        footerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Label linkLabel = new Label(link);
        linkLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-underline: true; -fx-cursor: hand;");
        bottom.getChildren().addAll(footerLabel, spacer2, linkLabel);

        card.getChildren().addAll(top, sep, bottom);
        return card;
    }

    // ==================== 4. GRAPHIQUES ====================
    private HBox createChartsRow() {
        HBox row = new HBox(20);

        // Ventes mensuelles (BarChart)
        VBox salesBox = new VBox(10);
        salesBox.setPadding(new Insets(20));
        salesBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        HBox salesHeader = new HBox(10);
        Label salesTitle = new Label("Ventes mensuelles");
        salesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        ComboBox<String> yearSelect = new ComboBox<>();
        yearSelect.getItems().addAll("Cette année", "Ce mois");
        yearSelect.getSelectionModel().select(0);
        yearSelect.setStyle("-fx-font-size: 12px;");
        salesHeader.getChildren().addAll(salesTitle, sp1, yearSelect);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setStyle("-fx-bar-fill: #E66239;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin"};
        double[] values = {125000, 150000, 180000, 220000, 210000, 280000};
        for (int i = 0; i < months.length; i++) {
            series.getData().add(new XYChart.Data<>(months[i], values[i]));
        }
        barChart.getData().add(series);
        barChart.setPrefHeight(300);

        salesBox.getChildren().addAll(salesHeader, barChart);

        // Aperçu clients (PieChart + stats)
        VBox customerBox = new VBox(10);
        customerBox.setPadding(new Insets(20));
        customerBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        HBox custHeader = new HBox(10);
        Label custTitle = new Label("Aperçu clients");
        custTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        ComboBox<String> globalSelect = new ComboBox<>();
        globalSelect.getItems().add("Global");
        globalSelect.getSelectionModel().select(0);
        globalSelect.setStyle("-fx-font-size: 12px;");
        custHeader.getChildren().addAll(custTitle, sp2, globalSelect);

        HBox custBody = new HBox(20);
        // Donut chart
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(
            new PieChart.Data("Nouveaux", firstTime),
            new PieChart.Data("Fidèles", returnCustomers)
        );
        pieChart.setLabelsVisible(true);
        pieChart.setPrefSize(220, 220);
        pieChart.setLegendVisible(false);

        // Stats clients
        VBox custStats = new VBox(15);
        custStats.setAlignment(Pos.CENTER);

        // Nouveaux vs Fidèles
        HBox nbRow = new HBox(20);
        VBox newBox = new VBox(5);
        newBox.setAlignment(Pos.CENTER);
        Label newNb = new Label(String.valueOf(firstTime));
        newNb.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label newLabel = new Label("Nouveaux");
        newLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        int newPct = Math.round((float) firstTime / totalCustomers * 100);
        Label newPctLabel = new Label("⬆ " + newPct + "%");
        newPctLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-background-color: #d1fae5; -fx-padding: 2 6; -fx-background-radius: 10;");
        newBox.getChildren().addAll(newNb, newLabel, newPctLabel);

        VBox retBox = new VBox(5);
        retBox.setAlignment(Pos.CENTER);
        Label retNb = new Label(String.valueOf(returnCustomers));
        retNb.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label retLabel = new Label("Fidèles");
        retLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px;");
        int retPct = Math.round((float) returnCustomers / totalCustomers * 100);
        Label retPctLabel = new Label("⬆ " + retPct + "%");
        retPctLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-background-color: #d1fae5; -fx-padding: 2 6; -fx-background-radius: 10;");
        retBox.getChildren().addAll(retNb, retLabel, retPctLabel);

        nbRow.getChildren().addAll(newBox, retBox);
        nbRow.setAlignment(Pos.CENTER);

        // Séparateur
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e0e0e0;");
        VBox.setMargin(sep, new Insets(10, 0, 10, 0));

        // Produits | Clients | Commandes
        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.getChildren().addAll(
            createStatBox(totalProduits, "Produits"),
            createStatBox(totalClients, "Clients"),
            createStatBox(totalCommandes, "Commandes")
        );

        custStats.getChildren().addAll(nbRow, sep, bottomRow);
        custBody.getChildren().addAll(pieChart, custStats);
        customerBox.getChildren().addAll(custHeader, custBody);

        row.getChildren().addAll(salesBox, customerBox);
        HBox.setHgrow(salesBox, Priority.ALWAYS);
        HBox.setHgrow(customerBox, Priority.ALWAYS);
        return row;
    }

    private VBox createStatBox(String value, String label) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");
        box.getChildren().addAll(val, lbl);
        return box;
    }

    // ==================== 5. LISTES ====================
    private HBox createListsRow() {
        HBox row = new HBox(20);

        row.getChildren().addAll(
            createTopProductsList(),
            createLowStockList(),
            createRecentSalesList()
        );
        for (int i = 0; i < 3; i++) HBox.setHgrow(row.getChildren().get(i), Priority.ALWAYS);
        return row;
    }

    private VBox createTopProductsList() {
        VBox box = new VBox(0);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        // En-tête
        HBox header = new HBox(10);
        header.setPadding(new Insets(15, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Top produits");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btn = new Button("📅 Annuel");
        btn.setStyle("-fx-font-size: 11px; -fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-radius: 4px;");
        header.getChildren().addAll(title, sp, btn);

        // Liste
        VBox list = new VBox(0);
        String[][] products = {
            {"Réfrigérateur Samsung", "250,000 FCFA", "12 unités"},
            {"Lave-linge LG",        "180,000 FCFA", "8 unités"},
            {"Climatiseur Haier",    "150,000 FCFA", "5 unités"},
            {"Cuisinière Whirlpool", "120,000 FCFA", "4 unités"},
            {"Micro-ondes Panasonic","95,000 FCFA",  "7 unités"}
        };
        for (String[] p : products) {
            list.getChildren().add(createProductItem(p[0], p[1], p[2], true));
        }
        if (products.length == 0) {
            list.getChildren().add(new Label("Aucune donnée") {{ setPadding(new Insets(15)); }});
        }

        box.getChildren().addAll(header, list);
        return box;
    }

    private HBox createProductItem(String name, String price, String qty, boolean isTop) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(12, 20, 12, 20));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");

        VBox left = new VBox(3);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        Label detail = new Label(price + " • " + qty);
        detail.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
        left.getChildren().addAll(nameLabel, detail);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label badge = new Label("Top");
        badge.setStyle(
            "-fx-text-fill: #667eea; -fx-font-size: 11px;" +
            "-fx-background-color: #eef2ff; -fx-padding: 2 8;" +
            "-fx-background-radius: 10; -fx-border-color: #667eea; -fx-border-radius: 10;"
        );

        item.getChildren().addAll(left, sp, badge);
        return item;
    }

    private VBox createLowStockList() {
        VBox box = new VBox(0);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        HBox header = new HBox(10);
        header.setPadding(new Insets(15, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Stock faible");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label link = new Label("Voir tout");
        link.setStyle("-fx-text-fill: #667eea; -fx-font-size: 12px; -fx-underline: true; -fx-cursor: hand;");
        header.getChildren().addAll(title, sp, link);

        VBox list = new VBox(0);
        String[][] stocks = {
            {"Micro-ondes", "3"},
            {"Bouilloire électrique", "5"},
            {"Grille-pain", "2"},
            {"Aspirateur", "4"},
            {"Fer à repasser", "1"}
        };
        for (String[] s : stocks) {
            HBox item = new HBox(10);
            item.setPadding(new Insets(12, 20, 12, 20));
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
            Label name = new Label(s[0]);
            Region sp2 = new Region();
            HBox.setHgrow(sp2, Priority.ALWAYS);
            VBox qtyBox = new VBox(3);
            qtyBox.setAlignment(Pos.CENTER);
            Label qtyLabel = new Label(s[1]);
            qtyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
            Label stockLabel = new Label("En stock");
            stockLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
            qtyBox.getChildren().addAll(qtyLabel, stockLabel);
            item.getChildren().addAll(name, sp2, qtyBox);
            list.getChildren().add(item);
        }
        if (stocks.length == 0) {
            list.getChildren().add(new Label("✅ Tous les stocks sont bons") {{ setPadding(new Insets(15)); }});
        }

        box.getChildren().addAll(header, list);
        return box;
    }

    private VBox createRecentSalesList() {
        VBox box = new VBox(0);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        HBox header = new HBox(10);
        header.setPadding(new Insets(15, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Dernières ventes");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btn = new Button("📅 Récent");
        btn.setStyle("-fx-font-size: 11px; -fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-radius: 4px;");
        header.getChildren().addAll(title, sp, btn);

        VBox list = new VBox(0);
        String[][] sales = {
            {"Alice Dupont", "Réfrigérateur Samsung", "250,000 FCFA"},
            {"Bob Martin",   "Lave-linge LG",         "180,000 FCFA"},
            {"Carla Gomez",  "Climatiseur Haier",     "150,000 FCFA"},
            {"David Lee",    "Cuisinière Whirlpool",  "120,000 FCFA"},
            {"Emma Stone",   "Micro-ondes Panasonic", "95,000 FCFA"}
        };
        for (String[] s : sales) {
            HBox item = new HBox(10);
            item.setPadding(new Insets(12, 20, 12, 20));
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");

            VBox left = new VBox(3);
            Label client = new Label(s[0]);
            client.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            Label produit = new Label(s[1]);
            produit.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
            left.getChildren().addAll(client, produit);

            Region sp2 = new Region();
            HBox.setHgrow(sp2, Priority.ALWAYS);

            Label montant = new Label(s[2]);
            montant.setStyle(
                "-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;" +
                "-fx-background-color: #d1fae5; -fx-padding: 3 8; -fx-background-radius: 10;"
            );

            item.getChildren().addAll(left, sp2, montant);
            list.getChildren().add(item);
        }
        if (sales.length == 0) {
            list.getChildren().add(new Label("Aucune vente récente") {{ setPadding(new Insets(15)); }});
        }

        box.getChildren().addAll(header, list);
        return box;
    }

    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Horloge optionnelle, on ne l'affiche pas dans cette version
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }
}