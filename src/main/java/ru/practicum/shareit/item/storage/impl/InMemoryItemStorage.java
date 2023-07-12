package ru.practicum.shareit.item.storage.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1L;

    @Override
    public Collection<Item> getAll(Long userId) {
        return items.values().stream()
                .filter((item) -> Objects.equals(item.getOwnerId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return findById(item.getId()).update(item);
    }

    @Override
    public Collection<Item> search(String text) {
        return items.values().stream()
                .filter((Item::getAvailable))
                .filter((item) -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
