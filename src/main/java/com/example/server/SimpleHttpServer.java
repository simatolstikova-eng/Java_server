package com.example.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.example.handlers.UserHandler;
import com.example.handlers.ProductHandler;
import com.example.handlers.AdminHandler;
import com.example.handlers.WebHandler;
import com.example.auth.CustomAuthenticator;
import com.example.storage.StorageManager;
import com.example.storage.UserStorage;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Используем общее хранилище
        UserStorage userStorage = StorageManager.getUserStorage();

        server.createContext("/api/users", new UserHandler());
        server.createContext("/api/products", new ProductHandler());

        HttpContext adminContext = server.createContext("/admin", new AdminHandler(userStorage));
        adminContext.setAuthenticator(new CustomAuthenticator("admin_realm"));

        // Передаём общее хранилище в WebHandler
        server.createContext("/", new WebHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
        System.out.println("Open http://localhost:8080 in your browser");
        System.out.println("Only users with 'admin' role can access");
    }
}