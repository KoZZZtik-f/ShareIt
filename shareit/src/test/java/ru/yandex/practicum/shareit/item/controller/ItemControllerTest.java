package ru.yandex.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.shareit.item.dto.CommentRequest;
import ru.yandex.practicum.shareit.item.dto.CommentResponse;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.mapper.CommentMapper;
import ru.yandex.practicum.shareit.item.mapper.ItemMapper;
import ru.yandex.practicum.shareit.item.model.Comment;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.item.service.ItemService;
import ru.yandex.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ItemDto itemDto;
    private Item item;
    private CommentRequest commentRequest;
    private CommentResponse commentResponse;
    private Comment comment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();

        User owner = new User(1L, "Owner", "owner@email.com");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        commentRequest = new CommentRequest();
        commentRequest.setText("Test comment");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());

        commentResponse = CommentMapper.toCommentResponse(comment);
    }

    @Test
    void addItem_ShouldReturnCreatedItem() throws Exception {
        when(itemService.addItem(any(Item.class), anyLong()))
                .thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void editItem_ShouldReturnUpdatedItem() throws Exception {
        //Given
        ItemDto updatedDto = new ItemDto();
        updatedDto.setName("Updated Item");
        updatedDto.setDescription("Updated Description");

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(true);

        //When
        when(itemService.editItem(anyLong(), any(Item.class), anyLong()))
                .thenReturn(updatedItem);

        //Then
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void getItemsByOwner_ShouldReturnItemsList() throws Exception {
        when(itemService.getItemsByOwner(anyLong()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() throws Exception {
        when(itemService.searchItems(anyString()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search?text=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        when(itemService.addComment(anyLong(), anyString(), anyLong()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"));
    }
}