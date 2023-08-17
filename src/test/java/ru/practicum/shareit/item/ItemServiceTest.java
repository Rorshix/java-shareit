package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.create.UnionService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemStorage itemStorage;

    @MockBean
    private CommentStorage commentStorage;

    @MockBean
    private BookingStorage bookingStorage;

    @MockBean
    private UserStorage userStorage;

    @MockBean
    private ItemRequestStorage itemRequestStorage;

    @MockBean
    private UnionService unionService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Booking firstBooking;
    private Booking secondBooking;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Anna")
                .email("anna@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest 1")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("screwdriver")
                .description("works well, does not ask to eat")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.returnItemDto(item);

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .created(LocalDateTime.now())
                .text("Thx, Cool item")
                .build();

        commentDto = CommentMapper.returnCommentDto(comment);

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addItem() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.addItem(user.getId(), itemDto);

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemNotBelongUser() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemStorage.findByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, item.getId(), user.getId()));
    }

    @Test
    void getItemById() {
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(bookingStorage.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(firstBooking));
        when(bookingStorage.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(secondBooking));
        when(commentStorage.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.getItemById(item.getId(), user.getId());

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void getItemsUser() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(unionService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));
        when(itemStorage.findByOwnerId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingStorage.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(firstBooking));
        when(bookingStorage.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(secondBooking));
        when(commentStorage.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.getItemsUser(user.getId(), 5, 10).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemStorage, times(1)).findByOwnerId(anyLong(), any(PageRequest.class));
    }

    @Test
    void searchItem() {
        when(unionService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));
        when(itemStorage.search(anyString(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(item)));

        ItemDto itemDtoTest = itemService.searchItem("text", 5, 10).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemStorage, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void searchItemEmptyText() {
        when(unionService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));

        List<ItemDto> itemDtoTest = itemService.searchItem("", 5, 10);

        assertTrue(itemDtoTest.isEmpty());

        verify(itemStorage, times(0)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void addComment() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.of(firstBooking));
        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDtoTest = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertEquals(commentDtoTest.getId(), comment.getId());
        assertEquals(commentDtoTest.getText(), comment.getText());
        assertEquals(commentDtoTest.getAuthorName(), comment.getAuthor().getName());

        verify(commentStorage, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentUserNotBookingItem() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }
}