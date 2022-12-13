package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CastomException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemServiceTest {
    @Mock
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    @Mock
    private final ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    @Mock
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    @Mock
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Mock
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    @Mock
    private final PageableCreater pageableCreater = Mockito.mock(PageableCreater.class);

    private ItemService itemService;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository, pageableCreater);
    }

    @Test
    void addItemTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true,
                user, null, null, null, null, 1L);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        assertEquals(1, itemService.addItem(item, 1).getId());
        assertEquals("name", itemService.addItem(item, 1).getName());
    }

    @Test
    void addItemIncorrectUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true,
                user, null, null, null, null, 1L);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemService.addItem(item, 1);
        });
        Assertions.assertEquals("user with id=1doesn't exist", thrown.getMessage());
    }

    @Test
    void getAllItemsTest() {
        User user = new User(1, "name", "email@mail.ru");
        /*Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, null, null);*/
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking1 = new Booking(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), item, null, null);
        Booking booking2 = new Booking(2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item, null, null);
        item.setLastBooking(booking1);
        item.setNextBooking(booking2);
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> page = new PageImpl<>(items);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findAllItemsByOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(page);
        Assertions.assertEquals(1, itemService.getAllItems(user.getId(), null, null).size());
    }

    @Test
    void getItemByIdTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, new Booking(), new Booking(), null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Assertions.assertEquals(1, itemService.getItemById(1, 1).getId());
        Assertions.assertEquals("name", itemService.getItemById(1, 1).getName());
    }

    @Test
    void getItemByIncorrectIdTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(2, 1);
        });
        Assertions.assertEquals("item with id=2 doesn't exist", thrown.getMessage());
    }

    @Test
    void updateItemNameTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Item newItem = new Item(1, "newName", "description", true, user,
                null, null, null, null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(newItem);
        Assertions.assertEquals("newName", itemService.updateItem(newItem, item.getId(), user.getId()).getName());
    }

    @Test
    void updateItemDescriptionTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Item newItem = new Item(1, "name", "newDescription", true, user,
                null, null, null, null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(newItem);
        Assertions.assertEquals("newDescription", itemService.updateItem(newItem, item.getId(), user.getId())
                .getDescription());
    }

    @Test
    void updateItemAvailableTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Item newItem = new Item(1, "name", "description", false, user,
                null, null, null, null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(newItem);
        Assertions.assertEquals(false, itemService.updateItem(newItem, item.getId(), user.getId())
                .getAvailable());
    }

    @Test
    void updateItemIncorrectOwnerTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Item newItem = new Item(1, "name", "description", false, user,
                null, null, null, null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(newItem);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(newItem, 1, 4);
        });
        Assertions.assertEquals("you're not owner!", thrown.getMessage());
    }

    @Test
    void searchItemTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> page = new PageImpl<>(items);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.search(Mockito.any(), Mockito.any())).thenReturn(page);
        Assertions.assertEquals(1, itemService.searchItem("desc", null, null).size());
    }

    @Test
    void deleteItemTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        itemService.deleteItem(1);
        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testCreateComment() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Comment comment = new Comment(1, "text", item, user, LocalDateTime.now());
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item, user, Status.APPROVED);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingByBookerAndItemAndEndBefore(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(booking);
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        Assertions.assertEquals(1, itemService.createComment(comment, 1, 1).getId());
    }

    @Test
    void testCreateEmptyComment() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Comment comment = new Comment(1, "", item, user, LocalDateTime.now());
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item, user, Status.APPROVED);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingByBookerAndItemAndEndBefore(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(booking);
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        Throwable thrown = assertThrows(CastomException.class, () -> {
            itemService.createComment(comment, 1, 4);
        });
        Assertions.assertEquals("comment is empty", thrown.getMessage());
    }

    @Test
    void testCreateBadComment() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Comment comment = new Comment(1, "dsfrg", item, user, LocalDateTime.now());
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item, user, Status.APPROVED);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        Throwable thrown = assertThrows(CastomException.class, () -> {
            itemService.createComment(comment, 1, 4);
        });
        Assertions.assertEquals("user can't commenting item that hasn't been booked", thrown.getMessage());
    }

    @Test
    void testFindAllByRequestId() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Item item2 = new Item(2, "name2", "description2", true, user,
                null, null, null, null, null);
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        Mockito.when(itemRequestRepository.getReferenceById(Mockito.anyLong())).thenReturn(new ItemRequest());
        Mockito.when(itemRepository.findItemsByRequest(Mockito.any())).thenReturn(items);
        Assertions.assertEquals(2, itemService.findItemsByRequestId(1L).size());
    }
}
