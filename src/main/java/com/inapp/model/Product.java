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
    
    public Product() {}
    
    public Product(int id, String name, String categoryName, double price, int quantity) {
        setId(id);
        setName(name);
        setCategoryName(categoryName);
        setPrice(price);
        setQuantity(quantity);
        setSku("PRD-" + String.format("%05d", id));
    }
    
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }
    
    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }
    
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
    
    public double getPurchasePrice() { return purchasePrice.get(); }
    public void setPurchasePrice(double v) { purchasePrice.set(v); }
    public DoubleProperty purchasePriceProperty() { return purchasePrice; }
    
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int v) { quantity.set(v); }
    public IntegerProperty quantityProperty() { return quantity; }
    
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
    
    public boolean isLowStock() {
        return quantity.get() <= minStock.get() && quantity.get() > 0;
    }
    
    public boolean isOutOfStock() {
        return quantity.get() <= 0;
    }
}