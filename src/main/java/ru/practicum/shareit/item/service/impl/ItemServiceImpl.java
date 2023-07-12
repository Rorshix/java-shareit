package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        return ItemMapper.toDto(itemStorage.getAll(userId));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toDto(itemStorage.findById(itemId));
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        var item = ItemMapper.fromDto(itemDto);
        item.setOwnerId(userService.getUserById(userId).getId());
        return ItemMapper.toDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (!Objects.equals(itemStorage.findById(itemId).getOwnerId(), userId)) {
            throw new NotFoundException("You not the owner of that item.");
        }
        var updatedItem = ItemMapper.fromDto(itemDto);
        updatedItem.setId(itemId);
        return ItemMapper.toDto(itemStorage.update(updatedItem));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return text.isEmpty() ? new ArrayList<>() : ItemMapper.toDto(itemStorage.search(text));
    }
}
