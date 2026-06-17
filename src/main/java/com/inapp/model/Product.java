package com.inapp.model;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class Product {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty categoryName = new SimpleStringProperty();
    private final IntegerProperty categoryId = new SimpleIntegerProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final DoubleProperty purchasePrice = new SimpleDoubleProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty(0);
    private final IntegerProperty minStock = new SimpleIntegerProperty(5);
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private final StringProperty imageUrl = new SimpleStringProperty();
    private final StringProperty sku = new SimpleStringProperty();
    private final StringProperty creator = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();

    public Product() {
        setStatus("Disponible");
    }

    public Product(int id, String name, String description, double price, int quantity, String category, String creator, String date) {
        setId(id);
        setName(name);
        setDescription(description);
        setPrice(price);
        setQuantity(quantity);
        setCategoryName(category);
        setCreator(creator);
        setDate(date);
        setSku("PRD-" + String.format("%05d", id));
        setStatus(quantity > 0 ? "Disponible" : "Rupture");
    }

    public Product(int id, String name, String categoryName, double price, int quantity) {
        setId(id);
        setName(name);
        setCategoryName(categoryName);
        setPrice(price);
        setQuantity(quantity);
        setSku("PRD-" + String.format("%05d", id));
        setStatus(quantity > 0 ? "Disponible" : "Rupture");
    }

    // ========== GETTERS ET SETTERS ==========
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }

    // Alias pour getName (utilisé par les vues Produit)
    public String getNomp() { return name.get(); }
    public void setNomp(String v) { name.set(v); }

    public String getDescription() { return description.get(); }
    public void setDescription(String v) { description.set(v); }
    public StringProperty descriptionProperty() { return description; }

    public String getCategoryName() { return categoryName.get(); }
    public void setCategoryName(String v) { categoryName.set(v); }
    public StringProperty categoryNameProperty() { return categoryName; }

    public int getCategoryId() { return categoryId.get(); }
    public void setCategoryId(int v) { categoryId.set(v); }
    public IntegerProperty categoryIdProperty() { return categoryId; }

    public double getPrice() { return price.get(); }
    public void setPrice(double v) { price.set(v); }
    public DoubleProperty priceProperty() { return price; }

    // Alias pour getPrice (utilisé par les vues Produit)
    public double getPrix() { return price.get(); }
    public void setPrix(double v) { price.set(v); }

    public double getPurchasePrice() { return purchasePrice.get(); }
    public void setPurchasePrice(double v) { purchasePrice.set(v); }
    public DoubleProperty purchasePriceProperty() { return purchasePrice; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int v) { 
        quantity.set(v);
        setStatus(v > 0 ? "Disponible" : "Rupture");
    }
    public IntegerProperty quantityProperty() { return quantity; }

    // Alias pour getQuantity (utilisé par les vues Produit)
    public int getQuantite() { return quantity.get(); }
    public void setQuantite(int v) { 
        quantity.set(v);
        setStatus(v > 0 ? "Disponible" : "Rupture");
    }

    public int getMinStock() { return minStock.get(); }
    public void setMinStock(int v) { minStock.set(v); }
    public IntegerProperty minStockProperty() { return minStock; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }

    public Image getImage() { return image.get(); }
    public void setImage(Image v) { image.set(v); }
    public ObjectProperty<Image> imageProperty() { return image; }

    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String v) { imageUrl.set(v); }
    public StringProperty imageUrlProperty() { return imageUrl; }

    public String getSku() { return sku.get(); }
    public void setSku(String v) { sku.set(v); }
    public StringProperty skuProperty() { return sku; }

    public String getCreator() { return creator.get(); }
    public void setCreator(String v) { creator.set(v); }
    public StringProperty creatorProperty() { return creator; }

    public String getDate() { return date.get(); }
    public void setDate(String v) { date.set(v); }
    public StringProperty dateProperty() { return date; }

    // ========== MÉTHODES UTILITAIRES ==========
    public int getStock() { return quantity.get(); }
    public void setStock(int v) { 
        quantity.set(v);
        setStatus(v > 0 ? "Disponible" : "Rupture");
    }

    public boolean isLowStock() {
        return quantity.get() <= minStock.get() && quantity.get() > 0;
    }

    public boolean isOutOfStock() {
        return quantity.get() <= 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}