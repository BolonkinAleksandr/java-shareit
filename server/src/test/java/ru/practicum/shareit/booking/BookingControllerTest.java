package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto;
    private BookingDto bookingDto;
    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime time = LocalDateTime.now();
        List<Comment> comments = new ArrayList<>();
        user = new User(1, "name", "email@mail.ru");
        UserDto userDto = new UserDto(1, "name", "email@mail.ru");
        comment = new Comment(1, "text", item, user, time);
        comments.add(comment);
        List<CommentDto> commentsDto = new ArrayList<>();
        commentDto = new CommentDto(1, "text", "name", time);
        commentsDto.add(commentDto);
        List<Item> items = new ArrayList<>();
        ItemRequest itemRequestForItem = new ItemRequest(1, "description", user, time, null);
        item = new Item(1, "name", "description", true, user, itemRequestForItem,
                null, null, comments, 1L);
        items.add(item);
        List<ItemDto> itemsDto = new ArrayList<>();
        itemDto = new ItemDto(1, "name", "description", true, userDto, 1L, commentsDto);
        itemsDto.add(itemDto);
        ItemRequest itemRequest = new ItemRequest(1, "description", user, time, null);
        itemRequestDto = new ItemRequestDto(1, "description", time, null);
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto(1, "name", "description",
                true, userDto, itemRequestDto, null, null, commentsDto);
        booking = new Booking(1, time, time.plusDays(1), item, user, Status.APPROVED);
        bookingDto = new BookingDto(1, 1, itemDto, userDto, time, time.plusDays(1), Status.APPROVED);
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(Mockito.any(), Mockito.anyLong()))
                .thenReturn(booking);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), long.class));
    }

    @Test
    void bookingApprovingTest() throws Exception {
        when(bookingService.bookingApproving(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(booking);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getAllBookingsByUserTest() throws Exception {
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingService.getAllBookingsByUser(Mockito.any(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingList);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getAllBookingItemsByUserTest() throws Exception {
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingService.getAllBookingItemsByUser(Mockito.any(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingList);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(bookingDto.getId()), long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }

}
