package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findById(Long userId);

    User findByEmail(String email);

    User create(User user);

    User update(Long userId, User user);

    void remove(Long userId);
}
