package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto;
    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private Comment comment;
    private CommentDto commentDto;

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
        itemRequest = new ItemRequest(1, "description", user, time, null);
        itemRequestDto = new ItemRequestDto(1, "description", time, null);
    }

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestService.addRequest(Mockito.any(), anyLong()))
                .thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getUserRequestsTest() throws Exception {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        when(itemRequestService.getUserRequests(Mockito.anyLong()))
                .thenReturn(itemRequestList);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), long.class));
    }

    @Test
    void getNotUserRequestsTest() throws Exception {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        when(itemRequestService.getNotUserRequests(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(itemRequestList);
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), long.class));
    }

}
