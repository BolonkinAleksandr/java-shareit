package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CastomException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Booking addBooking(Booking booking, long userId) {
        log.info("add booking {}", booking);
        checkUserById(userId);
        checkItemById(booking.getItem().getId());
        if (!itemRepository.getReferenceById(booking.getItem().getId()).getAvailable()) {
            throw new CastomException("item is not available");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getStart().isAfter(booking.getEnd()) ||
                booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new CastomException("incorrect data/time");
        }
        if (itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId() == userId) {
            throw new NotFoundException("owner can't booking item");
        }
        var user = userRepository.getReferenceById(userId);
        booking.setBooker(user);
        booking.setItem(itemRepository.getReferenceById(booking.getItem().getId()));
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking bookingApproving(long bookingId, Boolean isApproved, long userId) {
        log.info("booking approving");
        var owner = userRepository.getReferenceById(userId);
        var booking = bookingRepository.getReferenceById(bookingId);
        var item = itemRepository.getReferenceById(booking.getItem().getId());
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new CastomException("booking is approved already");
        }
        if (item.getOwner() != owner) {
            throw new NotFoundException("only owner can approve");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(long bookingId, long userId) {
        log.info("get booking information by booking id");
        var user = userRepository.getReferenceById(userId);
        checkBookingById(bookingId);
        var booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getBooker() != user && booking.getItem().getOwner() != user) {
            throw new NotFoundException("only owner or booking author can take a booking information");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUser(State state, long userId) {
        log.info("get all booking by user with id={}", userId);
        checkUserById(userId);
        var user = userRepository.getReferenceById(userId);
        try {
            switch (state) {
                case CURRENT:
                    return bookingRepository.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now());
                case PAST:
                    return bookingRepository.findBookingsByBookerAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                case FUTURE:
                    return bookingRepository.findBookingsByBookerAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                case WAITING:
                    return bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(user, Status.WAITING);
                case REJECTED:
                    return bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(user, Status.REJECTED);
                default:
                    return bookingRepository.findBookingsByBookerOrderByStartDesc(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Booking> getAllBookingItemsByUser(State state, long userId) {
        log.info("get all booking items by user with id={}", userId);
        checkUserById(userId);
        User user = userRepository.getReferenceById(userId);
        try {
            switch (state) {
                case CURRENT:
                    return bookingRepository.findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now());
                case PAST:
                    return bookingRepository.findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                case FUTURE:
                    return bookingRepository.findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                case WAITING:
                    return bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(user, Status.WAITING);
                case REJECTED:
                    return bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(user, Status.REJECTED);
                default:
                    return bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("user with id=" + userId + "doesn't exist");
        }
    }

    private void checkBookingById(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("booking with id=" + bookingId + " doesn't exist");
        }
    }

    private void checkItemById(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("item with id=" + itemId + " doesn't exist");
        }
    }
}
