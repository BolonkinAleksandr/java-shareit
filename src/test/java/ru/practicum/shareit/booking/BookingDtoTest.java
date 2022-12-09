package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingForItemDto;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;


    @Test
    void testItemDto() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "email@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, userDto, 1L,
                null);
        BookingDto bookingDto = new BookingDto(1L, 1L, itemDto, userDto, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);
        var jsonContent = json.write(bookingDto);
        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).hasJsonPath("$.item");
        assertThat(jsonContent).hasJsonPath("$.booker");
        assertThat(jsonContent).hasJsonPath("$.status");
        assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }

    @Test
    void bookingToBookingDtoTest() throws Exception {
        var user = new User(1, "name", "email@mail.ru");
        var item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item,
                null, null);
        BookingDto bookingDto = toBookingDto(booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStart(), bookingDto.getStart());
    }

    @Test
    void bookingDtoToBookingTest() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "email@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, userDto, 1L,
                null);
        BookingDto bookingDto = new BookingDto(1L, 1L, itemDto, userDto, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);
        Booking booking = toBooking(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStart(), bookingDto.getStart());
    }

    @Test
    void bookingToBookingToItemDtoTest() throws Exception {
        var user = new User(1, "name", "email@mail.ru");
        var user2 = new User(2, "name2", "email2@mail.ru");
        var item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item,
                user2, null);
        BookingForItemDto bookingDto = toBookingForItemDto(booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStart(), bookingDto.getStart());
    }
}
