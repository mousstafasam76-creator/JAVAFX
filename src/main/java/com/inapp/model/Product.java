package com.inapp.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Product {
    private final IntegerProperty id;
    private final StringProperty nomp;
    private final IntegerProperty prix;
    private final IntegerProperty quantite;
    private final StringProperty description;
    private final IntegerProperty createdBy;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;
    private final IntegerProperty categorieId;
    private final StringProperty categorieNom;

    public Product(int id, String nomp, int prix, int quantite, String description, 
                   int createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, 
                   int categorieId, String categorieNom) {
        this.id = new SimpleIntegerProperty(id);
        this.nomp = new SimpleStringProperty(nomp);
        this.prix = new SimpleIntegerProperty(prix);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.description = new SimpleStringProperty(description);
        this.createdBy = new SimpleIntegerProperty(createdBy);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt);
        this.categorieId = new SimpleIntegerProperty(categorieId);
        this.categorieNom = new SimpleStringProperty(categorieNom);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getNomp() { return nomp.get(); }
    public StringProperty nompProperty() { return nomp; }
    public void setNomp(String nomp) { this.nomp.set(nomp); }

    public int getPrix() { return prix.get(); }
    public IntegerProperty prixProperty() { return prix; }
    public void setPrix(int prix) { this.prix.set(prix); }

    public int getQuantite() { return quantite.get(); }
    public IntegerProperty quantiteProperty() { return quantite; }
    public void setQuantite(int quantite) { this.quantite.set(quantite); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String description) { this.description.set(description); }

    public int getCreatedBy() { return createdBy.get(); }
    public IntegerProperty createdByProperty() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy.set(createdBy); }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }

    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }

    public int getCategorieId() { return categorieId.get(); }
    public IntegerProperty categorieIdProperty() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId.set(categorieId); }

    public String getCategorieNom() { return categorieNom.get(); }
    public StringProperty categorieNomProperty() { return categorieNom; }
    public void setCategorieNom(String categorieNom) { this.categorieNom.set(categorieNom); }
}