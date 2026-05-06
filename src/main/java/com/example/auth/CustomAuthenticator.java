package com.example.auth;

import com.sun.net.httpserver.BasicAuthenticator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomAuthenticator extends BasicAuthenticator {
    private final Map<String, String> users = new ConcurrentHashMap<>();

    public CustomAuthenticator(String realm) {
        super(realm);
        users.put("admin", "admin123");
        users.put("user1", "password1");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        String expected = users.get(username);
        return expected != null && expected.equals(password);
    }
}