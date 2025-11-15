package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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
    void createItem_shouldReturn201() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("Drill", "Powerful drill", true, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null);

        when(itemService.createItem(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateItem_shouldReturn200() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Drill", null, null);
        ItemDto responseDto = new ItemDto(1L, "Updated Drill", "Powerful drill", true, null);

        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));
    }

    @Test
    void getItem_shouldReturn200() throws Exception {
        ItemWithBookingsDto responseDto = new ItemWithBookingsDto();
        responseDto.setId(1L);
        responseDto.setName("Drill");
        responseDto.setComments(Collections.emptyList());

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemsByOwner_shouldReturn200() throws Exception {
        ItemWithBookingsDto responseDto = new ItemWithBookingsDto();
        responseDto.setId(1L);
        responseDto.setComments(Collections.emptyList());

        when(itemService.getItemsByOwner(anyLong())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchItems_shouldReturn200() throws Exception {
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null);

        when(itemService.searchItems(anyString())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void addComment_shouldReturn200() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);
        responseDto.setText("Great item!");

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}