package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.create.Status;
import ru.practicum.shareit.create.UnionService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage itemRequestStorage;
    private final UnionService unionService;

    @Transactional
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {

        unionService.checkUser(userId);

        User user = userStorage.findById(userId).get();
        Item item = ItemMapper.returnItem(itemDto, user);

        if (itemDto.getRequestId() != null) {
            unionService.checkRequest(itemDto.getRequestId());
            item.setRequest(itemRequestStorage.findById(itemDto.getRequestId()).get());
        }
        itemStorage.save(item);

        return ItemMapper.returnItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {

        unionService.checkUser(userId);
        User user = userStorage.findById(userId).get();

        unionService.checkItem(itemId);
        Item item = ItemMapper.returnItem(itemDto, user);

        item.setId(itemId);

        if (!itemStorage.findByOwnerId(userId).contains(item)) {
            throw new NotFoundException(Item.class, "the item was not found with the user id " + userId);
        }

        Item newItem = itemStorage.findById(item.getId()).get();

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemStorage.save(newItem);

        return ItemMapper.returnItemDto(newItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId, long userId) {

        unionService.checkItem(itemId);
        Item item = itemStorage.findById(itemId).get();

        ItemDto itemDto = ItemMapper.returnItemDto(item);

        unionService.checkUser(userId);

        if (item.getOwner().getId() == userId) {

            Optional<Booking> lastBooking = bookingStorage.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingStorage.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.returnBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.returnBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }

        List<Comment> commentList = commentStorage.findAllByItemId(itemId);

        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.returnICommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsUser(long userId, Integer from, Integer size) {

        unionService.checkUser(userId);
        PageRequest pageRequest = unionService.checkPageSize(from, size);

        List<ItemDto> resultList = new ArrayList<>();

        for (ItemDto itemDto : ItemMapper.returnItemDtoList(itemStorage.findByOwnerId(userId, pageRequest))) {

            Optional<Booking> lastBooking = bookingStorage.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingStorage.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.returnBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.returnBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }

            resultList.add(itemDto);
        }

        for (ItemDto itemDto : resultList) {

            List<Comment> commentList = commentStorage.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(CommentMapper.returnICommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return resultList;
    }

    @Transactional(readOnly = true)
    @Override
    public  List<ItemDto> searchItem(String text, Integer from, Integer size) {

        PageRequest pageRequest = unionService.checkPageSize(from, size);

        if (text.equals("")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.returnItemDtoList(itemStorage.search(text, pageRequest));
        }
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {

        unionService.checkUser(userId);
        User user = userStorage.findById(userId).get();

        unionService.checkItem(itemId);
        Item item = itemStorage.findById(itemId).get();

        LocalDateTime dateTime = LocalDateTime.now();

        Optional<Booking> booking = bookingStorage.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, dateTime);

        if (booking.isEmpty()) {
            throw new ValidationException("User " + userId + " not booking this item " + itemId);
        }

        Comment comment = CommentMapper.returnComment(commentDto, item, user, dateTime);

        return CommentMapper.returnCommentDto(commentStorage.save(comment));
    }
}