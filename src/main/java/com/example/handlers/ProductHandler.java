package com.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.example.model.Product;
import com.example.storage.ProductStorage;
import com.example.util.JsonUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ProductHandler implements HttpHandler {
    private final ProductStorage storage = new ProductStorage();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("GET") && path.equals("/api/products")) {
            String json = JsonUtil.toJson(storage.findAll());
            sendResponse(exchange, 200, json);
        } else if (method.equals("GET") && path.startsWith("/api/products/")) {
            String id = path.substring("/api/products/".length());
            var product = storage.findById(id);
            if (product.isPresent()) {
                sendResponse(exchange, 200, JsonUtil.toJson(product.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Product not found\"}");
            }
        } else if (method.equals("POST") && path.equals("/api/products")) {
            String body = readBody(exchange);
            Product product = JsonUtil.fromJson(body, Product.class);
            product.setId(UUID.randomUUID().toString());
            storage.save(product);
            sendResponse(exchange, 201, JsonUtil.toJson(product));
        } else if (method.equals("PUT") && path.startsWith("/api/products/")) {
            String id = path.substring("/api/products/".length());
            String body = readBody(exchange);
            Product updated = JsonUtil.fromJson(body, Product.class);
            updated.setId(id);
            storage.update(id, updated);
            sendResponse(exchange, 200, JsonUtil.toJson(updated));
        } else if (method.equals("DELETE") && path.startsWith("/api/products/")) {
            String id = path.substring("/api/products/".length());
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