package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CastomException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    ItemRepository itemStorage;
    UserRepository userStorage;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;
    PageableCreater pageableCreater;

    @Autowired
    public ItemServiceImpl(ItemRepository itemStorage, UserRepository userStorage, BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemRequestRepository itemRequestRepository, PageableCreater pageableCreater) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.pageableCreater = pageableCreater;
    }

    @Transactional
    @Override
    public Item addItem(Item item, long ownerId) {
        log.info("add item {}", item);
        checkUserById(ownerId);
        item.setOwner(userStorage.getReferenceById(ownerId));
        if (item.getRequestId() != null) {
            item.setRequest(itemRequestRepository.getReferenceById(item.getRequestId()));
        }
        return itemStorage.save(item);
    }

    @Override
    public List<Item> getAllItems(long id, Integer from, Integer size) {
        log.info("get items by owner id={}", id);
        checkUserById(id);
        Pageable pageable = pageableCreater.doPageable(from, size);
        Page<Item> page = itemStorage.findAllItemsByOwnerId(id, pageable);
        List<Booking> bookings = bookingRepository.findAll();
        List<Item> items = page.toList();
        for (Item item : items) {
            if (item.getOwner().getId() == id) {
                Booking lastBooking = null;
                Booking nextBooking = null;
                for (Booking booking : bookings) {
                    if (booking.getItem().getId() == item.getId()) {
                        if ((booking.getEnd().isAfter(LocalDateTime.now()) &&
                                booking.getStart().isBefore(LocalDateTime.now())) ||
                                booking.getEnd().isBefore(LocalDateTime.now())) {
                            lastBooking = booking;
                        }
                    }
                }
                for (Booking booking : bookings) {
                    if (booking.getItem().getId() == item.getId()) {
                        if (booking.getStart().isAfter(LocalDateTime.now())) {
                            nextBooking = booking;
                        }
                    }
                }
                item.setLastBooking(lastBooking);
                item.setNextBooking(nextBooking);
            }
        }
        return items;
    }

    @Override
    public Item getItemById(long itemId, long userId) {
        log.info("get item by id={}", itemId);
        checkItemById(itemId);
        var item = itemStorage.getReferenceById(itemId);
        item.setComments(commentRepository.findCommentsByItemOrderByCreatedDesc(item));
        return addItemBookings(item, userId);
    }

    @Transactional
    @Override
    public Item updateItem(Item item, long id, long ownerId) {
        checkItemById(id);
        checkUserById(ownerId);
        Item oldItem = itemStorage.getReferenceById(id);
        if (itemStorage.getReferenceById(id).getOwner().getId() == ownerId) {
            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
        } else {
            throw new NotFoundException("you're not owner!");
        }
        return itemStorage.save(oldItem);
    }

    @Override
    public List<Item> searchItem(String text, Integer from, Integer size) {
        log.info("search item by text: {}", text);
        Pageable pageable = pageableCreater.doPageable(from, size);
        return itemStorage.search(text, pageable).toList();
    }

    @Override
    public void deleteItem(long id) {
        log.info("delete item with id={}", id);
        itemStorage.deleteById(id);
    }

    @Transactional
    @Override
    public Comment createComment(Comment comment, long itemId, long userId) {
        log.info("creating comment: {}", comment);
        checkItemById(itemId);
        checkUserById(userId);
        Booking booking = bookingRepository.findBookingByBookerAndItemAndEndBefore(
                userStorage.getReferenceById(userId),
                itemStorage.getReferenceById(itemId),
                LocalDateTime.now());
        if (comment.getText().isBlank()) {
            throw new CastomException("comment is empty");
        }
        if (booking != null) {
            comment.setAuthor(userStorage.getReferenceById(userId));
            comment.setItem(itemStorage.getReferenceById(itemId));
            comment.setCreated(LocalDateTime.now());
            return commentRepository.save(comment);
        } else {
            throw new CastomException("user can't commenting item that hasn't been booked");
        }
    }

    @Override
    public List<Item> findItemsByRequestId(Long requestId) {
        return itemStorage.findItemsByRequest(itemRequestRepository.getReferenceById(requestId));
    }

    private Item addItemBookings(Item item, long userId) {
        if (item.getOwner().getId() == userId) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> itemBookings = bookingRepository.findBookingsByItemOrderByStart(item);
            for (Booking booking : itemBookings) {
                if ((booking.getEnd().isAfter(LocalDateTime.now()) &&
                        booking.getStart().isBefore(LocalDateTime.now())) ||
                        booking.getEnd().isBefore(LocalDateTime.now())) {
                    lastBooking = booking;
                }
            }
            for (Booking booking : itemBookings) {
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    nextBooking = booking;
                }
            }
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }
        return item;
    }

    private void checkItemById(long itemId) {
        if (itemStorage.findById(itemId).isEmpty()) {
            throw new NotFoundException("item with id=" + itemId + " doesn't exist");
        }
    }

    private void checkUserById(long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NoSuchElementException("user with id=" + userId + "doesn't exist");
        }
    }
}