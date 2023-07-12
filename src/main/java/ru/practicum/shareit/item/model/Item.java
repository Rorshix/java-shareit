package ru.practicum.shareit.item.model;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;

    public Item update(Item item) {
        Optional.ofNullable(item.getName()).ifPresent((name) -> this.name = name);
        Optional.ofNullable(item.getDescription()).ifPresent((description) -> this.description = description);
        Optional.ofNullable(item.getAvailable()).ifPresent((available) -> this.available = available);
        return this;
    }
}
