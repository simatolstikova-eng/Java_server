package com.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.storage.UserStorage;
import com.example.model.User;
import java.io.*;
import java.util.List;

public class AdminHandler implements HttpHandler {
    private final UserStorage userStorage;

    public AdminHandler(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<User> users = userStorage.findAll();

        // Отладка
        System.out.println("=== AdminHandler ===");
        System.out.println("Users found: " + users.size());

        StringBuilder response = new StringBuilder();
        response.append("Admin Panel\n");
        response.append("Users:\n");

        for (User user : users) {
            response.append("- ").append(user.getUsername())
                    .append(" (").append(user.getRole()).append(")\n");
            System.out.println("  - " + user.getUsername() + " (" + user.getRole() + ")");
        }

        if (users.isEmpty()) {
            response.append("(no users found)\n");
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.toString().getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.toString().getBytes());
        }
    }
}