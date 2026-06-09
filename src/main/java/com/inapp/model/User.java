package com.inapp.model;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final StringProperty dateCreation = new SimpleStringProperty();
    private final StringProperty avatar = new SimpleStringProperty();
    
    public User() {}
    
    public User(int id, String username, String email, String role, String status) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setRole(role);
        setStatus(status);
        setDateCreation(java.time.LocalDateTime.now().toString());
    }
    
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }
    
    public String getUsername() { return username.get(); }
    public void setUsername(String v) { username.set(v); }
    public StringProperty usernameProperty() { return username; }
    
    public String getEmail() { return email.get(); }
    public void setEmail(String v) { email.set(v); }
    public StringProperty emailProperty() { return email; }
    
    public String getPassword() { return password.get(); }
    public void setPassword(String v) { password.set(v); }
    public StringProperty passwordProperty() { return password; }
    
    public String getRole() { return role.get(); }
    public void setRole(String v) { role.set(v); }
    public StringProperty roleProperty() { return role; }
    
    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }
    
    public String getNom() { return nom.get(); }
    public void setNom(String v) { nom.set(v); }
    public StringProperty nomProperty() { return nom; }
    
    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String v) { prenom.set(v); }
    public StringProperty prenomProperty() { return prenom; }
    
    public String getDateCreation() { return dateCreation.get(); }
    public void setDateCreation(String v) { dateCreation.set(v); }
    public StringProperty dateCreationProperty() { return dateCreation; }
    
    public String getAvatar() { return avatar.get(); }
    public void setAvatar(String v) { avatar.set(v); }
    public StringProperty avatarProperty() { return avatar; }
}