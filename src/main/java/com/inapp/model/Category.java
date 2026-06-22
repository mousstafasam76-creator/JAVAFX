package com.inapp.model;

import javafx.beans.property.*;

public class Category {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty(); // pending, approved, rejected
    private final StringProperty imageUrl = new SimpleStringProperty();
    private final IntegerProperty productsCount = new SimpleIntegerProperty(0);
    private final IntegerProperty createdBy = new SimpleIntegerProperty();
    private final IntegerProperty approvedBy = new SimpleIntegerProperty();
    private final StringProperty approvedAt = new SimpleStringProperty();
    private final StringProperty rejectionReason = new SimpleStringProperty();
    private final DoubleProperty stockValue = new SimpleDoubleProperty(0); // AJOUT

    public Category() {}

    public Category(int id, String name, String description, String status) {
        setId(id);
        setName(name);
        setDescription(description);
        setStatus(status);
    }

    public Category(int id, String name, String description, String status, String imageUrl) {
        this(id, name, description, status);
        setImageUrl(imageUrl);
    }

    // ========== GETTERS ET SETTERS ==========
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

    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String v) { imageUrl.set(v); }
    public StringProperty imageUrlProperty() { return imageUrl; }

    public int getProductsCount() { return productsCount.get(); }
    public void setProductsCount(int v) { productsCount.set(v); }
    public IntegerProperty productsCountProperty() { return productsCount; }

    public int getCreatedBy() { return createdBy.get(); }
    public void setCreatedBy(int v) { createdBy.set(v); }
    public IntegerProperty createdByProperty() { return createdBy; }

    public int getApprovedBy() { return approvedBy.get(); }
    public void setApprovedBy(int v) { approvedBy.set(v); }
    public IntegerProperty approvedByProperty() { return approvedBy; }

    public String getApprovedAt() { return approvedAt.get(); }
    public void setApprovedAt(String v) { approvedAt.set(v); }
    public StringProperty approvedAtProperty() { return approvedAt; }

    public String getRejectionReason() { return rejectionReason.get(); }
    public void setRejectionReason(String v) { rejectionReason.set(v); }
    public StringProperty rejectionReasonProperty() { return rejectionReason; }

    // ========== MÉTHODES POUR stockValue ==========
    public double getStockValue() { return stockValue.get(); }
    public void setStockValue(double v) { stockValue.set(v); }
    public DoubleProperty stockValueProperty() { return stockValue; }

    @Override
    public String toString() {
        return getName();
    }
}