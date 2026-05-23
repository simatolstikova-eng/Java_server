package com.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;

public class WebHandler implements HttpHandler {
    private static final String WEB_ROOT = "web/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/") || path.equals("/index.html")) {
            String auth = exchange.getRequestHeaders().getFirst("Authorization");

            if (auth == null || !auth.startsWith("Basic ")) {
                sendAuthRequired(exchange);
                return;
            }

            String decoded = new String(Base64.getDecoder().decode(auth.substring(6)));
            String[] parts = decoded.split(":", 2);
            String username = parts[0];
            String password = parts[1];

            if (!"admin".equals(username) || !"admin123".equals(password)) {
                sendAuthRequired(exchange);
                return;
            }

            sendFile(exchange, WEB_ROOT + "index.html", "text/html");
            return;
        }

        if (!path.startsWith("/api/")) {
            String filePath = WEB_ROOT + path;
            sendFile(exchange, filePath, getContentType(path));
            return;
        }

        exchange.sendResponseHeaders(404, -1);
        exchange.close();
    }

    private void sendAuthRequired(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"Admin Panel\"");
        exchange.sendResponseHeaders(401, -1);
        exchange.close();
    }

    private void sendFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }

        byte[] data = Files.readAllBytes(Paths.get(filePath));
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, data.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        return "text/plain";
    }
}