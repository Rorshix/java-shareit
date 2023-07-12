package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Collection<Item> getAll(Long userId);

    Item findById(Long itemId);

    Item create(Item item);

    Item update(Item item);

    Collection<Item> search(String text);
}