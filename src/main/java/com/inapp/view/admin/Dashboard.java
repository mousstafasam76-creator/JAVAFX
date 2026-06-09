package com.inapp.view.admin;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Dashboard extends BorderPane {
    
    private Label clockLabel;
    private Timeline clockTimeline;
    
    public Dashboard() {
        setupUI();
        startClock();
    }
    
    private void setupUI() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e9ecef);");
        
        // Section bienvenue avec horloge
        HBox welcomeSection = createWelcomeSection();
        
        // Cartes statistiques
        GridPane statsGrid = createStatsGrid();
        
        // Graphiques
        HBox chartsRow = createChartsRow();
        
        // Dernières activités
        VBox recentActivities = createRecentActivities();
        
        content.getChildren().addAll(welcomeSection, statsGrid, chartsRow, recentActivities);
        
        // Animation d'entrée
        content.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), content);
        tt.setFromY(30);
        tt.setToY(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        tt.play();
        ft.play();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setCenter(scrollPane);

    }
    
    private HBox createWelcomeSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-background-radius: 20px;");
        
        VBox textBox = new VBox(10);
        Label welcomeLabel = new Label("Bonjour, Super Admin !");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        Label dateLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy")));
        dateLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px;");
        
        clockLabel = new Label();
        clockLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
        
        textBox.getChildren().addAll(welcomeLabel, dateLabel, clockLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label emojiLabel = new Label("📊");
        emojiLabel.setStyle("-fx-font-size: 64px;");
        
        section.getChildren().addAll(textBox, spacer, emojiLabel);
        return section;
    }
    
    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        String[][] stats = {
            {"👥", "Utilisateurs", "1,234", "+12%", "#667eea"},
            {"📦", "Catégories", "45", "+5%", "#28a745"},
            {"🛒", "Produits", "1,890", "+18%", "#17a2b8"},
            {"💰", "Ventes", "125,000 FCFA", "+25%", "#ffc107"}
        };
        
        for (int i = 0; i < stats.length; i++) {
            VBox card = createAnimatedStatCard(stats[i][0], stats[i][1], stats[i][2], stats[i][3], stats[i][4]);
            grid.add(card, i, 0);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }
        
        return grid;
    }
    
    private VBox createAnimatedStatCard(String icon, String title, String value, String trend, String color) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label trendLabel = new Label(trend);
        trendLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px;");
        
        card.getChildren().addAll(header, valueLabel, trendLabel);
        
        // Animation au survol
        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);");
        });
        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1);
            st.setToY(1);
            st.play();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        });
        
        return card;
    }
    
    private HBox createChartsRow() {
        HBox row = new HBox(20);
        
        // Graphique des ventes
        VBox salesChart = createSalesChart();
        
        // Graphique circulaire
        VBox pieChart = createPieChart();
        
        row.getChildren().addAll(salesChart, pieChart);
        HBox.setHgrow(salesChart, Priority.ALWAYS);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        
        return row;
    }
    
    private VBox createSalesChart() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        
        Label title = new Label("📈 Évolution des ventes");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Jan", 125000));
        series.getData().add(new XYChart.Data<>("Fév", 150000));
        series.getData().add(new XYChart.Data<>("Mar", 180000));
        series.getData().add(new XYChart.Data<>("Avr", 220000));
        series.getData().add(new XYChart.Data<>("Mai", 210000));
        series.getData().add(new XYChart.Data<>("Juin", 280000));
        
        barChart.getData().add(series);
        barChart.setPrefHeight(250);
        
        card.getChildren().addAll(title, barChart);
        return card;
    }
    
    private VBox createPieChart() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        
        Label title = new Label("🥧 Répartition des ventes");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(
            new PieChart.Data("Électronique", 45),
            new PieChart.Data("Mode", 25),
            new PieChart.Data("Maison", 20),
            new PieChart.Data("Autres", 10)
        );
        pieChart.setLabelsVisible(true);
        pieChart.setPrefHeight(250);
        
        card.getChildren().addAll(title, pieChart);
        return card;
    }
    
    private VBox createRecentActivities() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        
        Label title = new Label("🕐 Dernières activités");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox activities = new VBox(10);
        String[][] acts = {
            {"Nouvel utilisateur inscrit", "Il y a 5 minutes", "#28a745"},
            {"Nouvelle catégorie ajoutée", "Il y a 2 heures", "#17a2b8"},
            {"Produit mis à jour", "Il y a 3 heures", "#ffc107"},
            {"Vente enregistrée", "Il y a 5 heures", "#667eea"}
        };
        
        for (String[] act : acts) {
            HBox activity = new HBox(15);
            activity.setAlignment(Pos.CENTER_LEFT);
            activity.setPadding(new Insets(10, 0, 10, 0));
            activity.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
            
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill: " + act[2] + "; -fx-font-size: 12px;");
            
            Label text = new Label(act[0]);
            text.setStyle("-fx-font-size: 13px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label time = new Label(act[1]);
            time.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
            
            activity.getChildren().addAll(dot, text, spacer, time);
            activities.getChildren().add(activity);
        }
        
        card.getChildren().addAll(title, activities);
        return card;
    }
    
    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }
}