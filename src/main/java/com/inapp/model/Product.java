package com.inapp.view.front.Produit;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty price;
    private final IntegerProperty stock;
    private final StringProperty category;
    private final StringProperty creator;
    private final StringProperty date;

    public Product(int id, String name, String description, double price, int stock, String category, String creator, String date) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description.length() > 50 ? description.substring(0, 47) + "..." : description);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.category = new SimpleStringProperty(category);
        this.creator = new SimpleStringProperty(creator);
        this.date = new SimpleStringProperty(date);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }

    public int getStock() { return stock.get(); }
    public IntegerProperty stockProperty() { return stock; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public String getCreator() { return creator.get(); }
    public StringProperty creatorProperty() { return creator; }

    public String getDate() { return date.get(); }
    public StringProperty dateProperty() { return date; }

    public void setName(String name) { this.name.set(name); }
    public void setDescription(String description) { this.description.set(description); }
    public void setPrice(double price) { this.price.set(price); }
    public void setStock(int stock) { this.stock.set(stock); }
    public void setCategory(String category) { this.category.set(category); }
}