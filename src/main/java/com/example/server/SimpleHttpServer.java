package com.example.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.example.handlers.UserHandler;
import com.example.handlers.ProductHandler;
import com.example.handlers.AdminHandler;
import com.example.auth.CustomAuthenticator;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/users", new UserHandler());
        server.createContext("/api/products", new ProductHandler());
        HttpContext adminContext = server.createContext("/admin", new AdminHandler());
        adminContext.setAuthenticator(new CustomAuthenticator("admin_realm"));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}