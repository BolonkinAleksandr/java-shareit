package ru.practicum.shareit.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        var item = new Item();
        item.setId(bookingDto.getItemId());
        return Booking.builder()
                .id(bookingDto.getId())
                .booker(bookingDto.getBooker() != null ? toUser(bookingDto.getBooker()) : null)
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(item)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .booker(booking.getBooker() != null ? toUserDto(booking.getBooker()) : null)
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .id(booking.getId())
                .item(toItemDto(booking.getItem()))
                .build();
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .id(booking.getId())
                .build();
    }
}
