package ru.practicum.shareit.user.storage.impl;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1L;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long userId) {
        var user = users.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with %d id not found.", userId));
        }
        return users.get(userId);
    }

    @Override
    public User findByEmail(String email) {
        for (var user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        return findById(userId).update(user);
    }

    @Override
    public void remove(Long userId) {
        users.remove(userId);
    }
}
