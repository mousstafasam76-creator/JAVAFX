package com.inapp.model;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class Category {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final IntegerProperty productsCount = new SimpleIntegerProperty(0);
    private final DoubleProperty stockValue = new SimpleDoubleProperty(0);
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private final StringProperty imageUrl = new SimpleStringProperty();
    
    public Category() {}
    
    public Category(int id, String name, String description, String status) {
        setId(id);
        setName(name);
        setDescription(description);
        setStatus(status);
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
    
    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }
    
    public int getProductsCount() { return productsCount.get(); }
    public void setProductsCount(int v) { productsCount.set(v); }
    public IntegerProperty productsCountProperty() { return productsCount; }
    
    public double getStockValue() { return stockValue.get(); }
    public void setStockValue(double v) { stockValue.set(v); }
    public DoubleProperty stockValueProperty() { return stockValue; }
    
    public Image getImage() { return image.get(); }
    public void setImage(Image v) { image.set(v); }
    public ObjectProperty<Image> imageProperty() { return image; }
    
    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String v) { imageUrl.set(v); }
    public StringProperty imageUrlProperty() { return imageUrl; }
}