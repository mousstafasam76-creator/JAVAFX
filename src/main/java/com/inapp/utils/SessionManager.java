package com.inapp.utils;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    
    private static SessionManager instance;
    private Map<String, Object> sessionData;
    private boolean isLoggedIn;
    private long loginTime;
    
    private SessionManager() {
        sessionData = new HashMap<>();
        isLoggedIn = false;
        loginTime = 0;
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setUser(int userId, String username, String role, String email, String fullName) {
        sessionData.put("user_id", userId);
        sessionData.put("username", username);
        sessionData.put("user_role", role);
        sessionData.put("user_email", email);
        sessionData.put("user_name", fullName);
        String[] names = fullName.split(" ", 2);
        sessionData.put("prenom", names[0]);
        sessionData.put("nom", names.length > 1 ? names[1] : "");
        sessionData.put("login_time", System.currentTimeMillis());
        isLoggedIn = true;
        loginTime = System.currentTimeMillis();
    }
    
    public Map<String, Object> getCurrentUser() {
        return sessionData;
    }
    
    public int getCurrentUserId() {
        return (int) sessionData.getOrDefault("user_id", 0);
    }
    
    public String getCurrentUserRole() {
        return (String) sessionData.getOrDefault("user_role", "");
    }
    
    public String getCurrentUsername() {
        return (String) sessionData.getOrDefault("username", "");
    }
    
    public String getCurrentUserEmail() {
        return (String) sessionData.getOrDefault("user_email", "");
    }
    
    public String getCurrentUserFullName() {
        return (String) sessionData.getOrDefault("user_name", "");
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public boolean isSuperAdmin() {
        return "super_admin".equals(getCurrentUserRole());
    }
    
    public boolean isAdmin() {
        return "admin".equals(getCurrentUserRole()) || isSuperAdmin();
    }
    
    public long getSessionDuration() {
        if (!isLoggedIn) return 0;
        return (System.currentTimeMillis() - loginTime) / 1000;
    }
    
    public void clearSession() {
        sessionData.clear();
        isLoggedIn = false;
        loginTime = 0;
    }
    
    public Object get(String key) {
        return sessionData.get(key);
    }
    
    public void set(String key, Object value) {
        sessionData.put(key, value);
    }
    
    public boolean hasPermission(String requiredRole) {
        String userRole = getCurrentUserRole();
        if ("super_admin".equals(userRole)) return true;
        return requiredRole.equals(userRole);
    }
}