package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking, long userId);

    Booking bookingApproving(long bookingId, Boolean isApproved, long userId);

    Booking getById(long bookingId, long userId);

    List<Booking> getAllBookingsByUser(State state, long userId);

    List<Booking> getAllBookingItemsByUser(State state, long userId);
}
