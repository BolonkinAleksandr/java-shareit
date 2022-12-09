package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CastomException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingServiceTest {
    @Mock
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    @Mock
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Mock
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    @Mock
    private final PageableCreater pageableCreater = Mockito.mock(PageableCreater.class);

    private BookingService bookingService;
    private LocalDateTime time;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, pageableCreater);
        time = LocalDateTime.now();
    }


    @Test
    void addBookingTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusDays(1), time.plusDays(2), item, null, null);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        assertEquals(1, bookingService.addBooking(booking, 2).getId());
        assertEquals(booking.getItem().getId(), bookingService.addBooking(booking, 2).getItem().getId());
        assertEquals(booking.getStatus(), bookingService.addBooking(booking, 2).getStatus());
        assertEquals(booking.getBooker().getId(), bookingService.addBooking(booking, 2).getBooker().getId());
    }

    @Test
    void addBookingByOwnerTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusDays(1), time.plusDays(2), item, null, null);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {bookingService.addBooking(booking, 1);});
        Assertions.assertEquals("owner can't booking item", thrown.getMessage());
    }

    @Test
    void addBookingIncorrectDataTimeTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time, time.plusDays(2), item, null, null);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(CastomException.class, () -> {bookingService.addBooking(booking, 2);});
        Assertions.assertEquals("incorrect data/time", thrown.getMessage());
    }

    @Test
    void addBookingIncorrectAvailableTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", false, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, null);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(CastomException.class, () -> {bookingService.addBooking(booking, 2);});
        Assertions.assertEquals("item is not available", thrown.getMessage());
    }

    @Test
    void addBookingIncorrectUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, null);
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {bookingService.addBooking(booking, 3);});
        Assertions.assertEquals("user with id=3 doesn't exist", thrown.getMessage());
    }

    @Test
    void addBookingIncorrectItemTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, null);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {bookingService.addBooking(booking, 2);});
        Assertions.assertEquals("item with id=1 doesn't exist", thrown.getMessage());
    }

    @Test
    void bookingApprovingTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.WAITING);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Assertions.assertEquals(Status.APPROVED, bookingService.bookingApproving(1, true, 1).getStatus());
    }

    @Test
    void bookingApprovingIncorrectOwnerTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.WAITING);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {bookingService.bookingApproving(1, true, 2);});
        Assertions.assertEquals("only owner can approve", thrown.getMessage());
    }

    @Test
    void bookingApprovingIncorrectStatusTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        Mockito.when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Throwable thrown = assertThrows(CastomException.class, () -> {bookingService.bookingApproving(1, true, 1);});
        Assertions.assertEquals("booking is approved already", thrown.getMessage());
    }

    @Test
    void getByIdTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.WAITING);
        Mockito.when(bookingRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking);
        Assertions.assertEquals(1, bookingService.getById(1, 1).getId());
    }

    @Test
    void getByIdIncorrectOwnerTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        Mockito.when(bookingRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {bookingService.getById(1, 2);});
        Assertions.assertEquals("only owner or booking author can take a booking information", thrown.getMessage());
    }

    @Test
    void getByIdIncorrectBookingIdTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {bookingService.getById(1, 2);});
        Assertions.assertEquals("booking with id=1 doesn't exist", thrown.getMessage());
    }

    @Test
    void getAllBookingsByUserDefaultTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findBookingsByBookerOrderByStartDesc(Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingsByUser(State.valueOf("ALL"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingsByUser(State.valueOf("ALL"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingsByUserCurrentTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.any(), Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingsByUser(State.valueOf("CURRENT"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingsByUser(State.valueOf("CURRENT"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingsByUserPastTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findBookingsByBookerAndEndBeforeOrderByStartDesc(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingsByUser(State.valueOf("PAST"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingsByUser(State.valueOf("PAST"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingsByUserFutureTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findBookingsByBookerAndStartAfterOrderByStartDesc(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingsByUser(State.valueOf("FUTURE"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingsByUser(State.valueOf("FUTURE"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingsByUserWaitingTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingsByUser(State.valueOf("WAITING"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingsByUser(State.valueOf("WAITING"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingsByUserRejectedTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingsByUser(State.valueOf("REJECTED"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingsByUser(State.valueOf("REJECTED"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingItemsByUserDefaultTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingItemsByUser(State.valueOf("ALL"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingItemsByUser(State.valueOf("ALL"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingItemsByUserCURRENTTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.any(),
                        Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingItemsByUser(State.valueOf("CURRENT"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingItemsByUser(State.valueOf("CURRENT"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingItemsByUserPASTTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingItemsByUser(State.valueOf("PAST"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingItemsByUser(State.valueOf("PAST"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingItemsByUserFUTURETest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingItemsByUser(State.valueOf("FUTURE"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingItemsByUser(State.valueOf("FUTURE"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingItemsByUserWAITINGTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingItemsByUser(State.valueOf("WAITING"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingItemsByUser(State.valueOf("WAITING"), 1,
                null, null).get(0));
    }

    @Test
    void getAllBookingItemsByUserREJECTEDTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, time.plusHours(1), time.plusDays(2), item, null, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(page);
        Assertions.assertEquals(1, bookingService.getAllBookingItemsByUser(State.valueOf("REJECTED"), 1,
                null, null).size());
        Assertions.assertEquals(booking, bookingService.getAllBookingItemsByUser(State.valueOf("REJECTED"), 1,
                null, null).get(0));
    }
}
