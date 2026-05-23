package com.example.storage;

public class StorageManager {
    private static UserStorage userStorage = null;
    private static ProductStorage productStorage = null;

    public static UserStorage getUserStorage() {
        if (userStorage == null) {
            userStorage = new UserStorage();
        }
        return userStorage;
    }

    public static ProductStorage getProductStorage() {
        if (productStorage == null) {
            productStorage = new ProductStorage();
        }
        return productStorage;
    }
}