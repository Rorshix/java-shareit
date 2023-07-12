package ru.practicum.shareit.user.storage.impl;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private static long newId = 1;

    private final HashMap<Long, User> userMap = new HashMap<>();

    @Override
    public User get(long userId) {

        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        } else {
            throw new NotFoundException( "User id " + userId + " not found.");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User add(User user) {

        for (User userCheckEmail : getAll()) {
            if (userCheckEmail.getEmail().equals(user.getEmail())) {
                throw new WrongAccessException("there is already a user with an email " + user.getEmail());
            }
        }

        if (user.getId() == 0) {
            user.setId(newId++);
        }

        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, long userId) {

        User newUser = userMap.get(userId);

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            for (User userCheckEmail : getAll()) {
                if (userCheckEmail.getEmail().equals(user.getEmail()) && userCheckEmail.getId() != userId) {
                    throw new WrongAccessException("there is already a user with an email " + user.getEmail());
                }
            }

            newUser.setEmail(user.getEmail());
        }

        userMap.put(userId, newUser);
        return userMap.get(user.getId());
    }

    @Override
    public void delete(User user) {

        if (!userMap.containsValue(user)) {
            throw new NotFoundException( "User id " + user.getId() + " not found.");
        }

        userMap.remove(user.getId());
    }
}