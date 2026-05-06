package com.example.storage;

import com.example.model.User;

public class UserStorage extends Storage<User> {
    public UserStorage() {
        super("data/users.json");
    }

    @Override
    protected String getId(User user) {
        return user.getId();
    }
}