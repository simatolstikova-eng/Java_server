package com.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.storage.UserStorage;
import java.io.*;
import java.util.Base64;

public class AdminHandler implements HttpHandler {
    private final UserStorage userStorage = new UserStorage();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Basic ")) {
            sendChallenge(exchange);
            return;
        }
        String decoded = new String(Base64.getDecoder().decode(auth.substring(6)));
        String[] parts = decoded.split(":", 2);
        String username = parts[0];

        boolean isAdmin = userStorage.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username) && "admin".equals(user.getRole()));

        if (!isAdmin) {
            sendResponse(exchange, 403, "Forbidden");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Admin Panel\nUsers:\n");
        userStorage.findAll().forEach(u -> sb.append("- ").append(u.getUsername()).append(" (").append(u.getRole()).append(")\n"));
        sendResponse(exchange, 200, sb.toString());
    }

    private void sendChallenge(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"admin\"");
        exchange.sendResponseHeaders(401, -1);
        exchange.close();
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        exchange.sendResponseHeaders(code, body.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes());
        }
    }
}