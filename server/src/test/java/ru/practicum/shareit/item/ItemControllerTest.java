package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("Item", "Description", true, null);
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null);

        when(itemService.createItem(anyLong(), any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));

        verify(itemService).createItem(eq(1L), any(ItemCreateDto.class));
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        ItemWithBookingsDto itemDto = new ItemWithBookingsDto(
                1L, "Item", "Description", true, null, null, null, Collections.emptyList()
        );

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));

        verify(itemService).getItem(1L, 1L);
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated", null, null);
        ItemDto itemDto = new ItemDto(1L, "Updated", "Description", true, null);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(itemService).updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Drill", "Power drill", true, null)
        );

        when(itemService.searchItems(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService).searchItems("drill");
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
        CommentDto savedComment = new CommentDto(
                1L, "Great item!", "User", LocalDateTime.now()
        );

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(savedComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));

        verify(itemService).addComment(eq(1L), eq(1L), any(CommentDto.class));
    }
}