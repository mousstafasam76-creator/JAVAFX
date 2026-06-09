package com.inapp.service;

import com.inapp.model.User;
import com.inapp.utils.SessionManager;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    
    private Map<String, User> mockUsers;
    
    public AuthService() {
        mockUsers = new HashMap<>();
        User admin = new User(1, "admin", "admin@inapp.com", "super_admin", "active");
        admin.setPassword("admin");
        mockUsers.put("admin", admin);
        
        User user = new User(2, "user", "user@inapp.com", "admin", "active");
        user.setPassword("user");
        mockUsers.put("user", user);
    }
    
    public User authenticate(String username, String password) {
        User user = mockUsers.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}