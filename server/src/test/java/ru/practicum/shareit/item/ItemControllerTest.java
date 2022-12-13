package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto;
    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    ItemRequestDto itemRequestDto;
    ItemWithBookingDto itemWithBookingDto;

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
        itemWithBookingDto = new ItemWithBookingDto(1, "name", "description",
                true, userDto, itemRequestDto, null, null, commentsDto);
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(item);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(item);
        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void searchItemTest() throws Exception {
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemService.searchItem(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemList);
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "description")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), long.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void testFindAllItem() throws Exception {
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemService.getAllItems(
                Mockito.anyLong(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(itemList);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), long.class));
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.createComment(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(comment);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void deleteItemTest() throws Exception {
        mvc.perform(delete("/items/{id}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito
                .verify(itemService, Mockito.times(1))
                .deleteItem(Mockito.anyLong());
    }
}
