package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.mapper.BookingMapper.toBookingDto;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toBookingDto(service.addBooking(toBooking(bookingDto), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@PathVariable long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") long userId,
                               @RequestParam Boolean approved) {
        return toBookingDto(service.bookingApproving(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toBookingDto(service.getById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDto> bookingsDto = new ArrayList();
        State enumState;
        try {
            enumState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        List<Booking> bookings = service.getAllBookingsByUser(enumState, userId);
        for (Booking booking : bookings) {
            bookingsDto.add(toBookingDto(booking));
        }
        return bookingsDto;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingItemsByUser(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDto> bookingsDto = new ArrayList();
        State enumState;
        try {
            enumState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        List<Booking> bookings = service.getAllBookingItemsByUser(enumState, userId);
        for (Booking booking : bookings) {
            bookingsDto.add(toBookingDto(booking));
        }
        return bookingsDto;
    }
}
