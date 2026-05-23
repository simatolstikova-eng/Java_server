package com.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.model.User;
import com.example.storage.UserStorage;
import com.example.util.JsonUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserHandler implements HttpHandler {
    private final UserStorage storage;

    public UserHandler() {
        this.storage = new UserStorage();
    }

    public UserHandler(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("GET") && path.equals("/api/users")) {
            String json = JsonUtil.toJson(storage.findAll());
            sendResponse(exchange, 200, json);
        } else if (method.equals("GET") && path.startsWith("/api/users/")) {
            String id = path.substring("/api/users/".length());
            var user = storage.findById(id);
            if (user.isPresent()) {
                sendResponse(exchange, 200, JsonUtil.toJson(user.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
            }
        } else if (method.equals("POST") && path.equals("/api/users")) {
            String body = readBody(exchange);
            User user = JsonUtil.fromJson(body, User.class);
            user.setId(UUID.randomUUID().toString());
            storage.save(user);
            sendResponse(exchange, 201, JsonUtil.toJson(user));
        } else if (method.equals("PUT") && path.startsWith("/api/users/")) {
            String id = path.substring("/api/users/".length());
            String body = readBody(exchange);
            User updated = JsonUtil.fromJson(body, User.class);
            updated.setId(id);
            storage.update(id, updated);
            sendResponse(exchange, 200, JsonUtil.toJson(updated));
        } else if (method.equals("DELETE") && path.startsWith("/api/users/")) {
            String id = path.substring("/api/users/".length());
            storage.delete(id);
            sendResponse(exchange, 204, "");
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}