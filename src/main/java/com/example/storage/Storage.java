package com.example.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public abstract class Storage<T> {
    protected final String filePath;
    protected final ObjectMapper objectMapper;
    protected Map<String, T> storage;

    public Storage(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.storage = new HashMap<>();
        loadFromFile();
    }

    protected abstract String getId(T item);

    private void loadFromFile() {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                List<T> items = objectMapper.readValue(file, new TypeReference<List<T>>() {});
                for (T item : items) {
                    storage.put(getId(item), item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(filePath), new ArrayList<>(storage.values()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void save(T item) {
        storage.put(getId(item), item);
        saveToFile();
    }

    public void update(String id, T item) {
        if (storage.containsKey(id)) {
            storage.put(id, item);
            saveToFile();
        }
    }

    public void delete(String id) {
        storage.remove(id);
        saveToFile();
    }
}