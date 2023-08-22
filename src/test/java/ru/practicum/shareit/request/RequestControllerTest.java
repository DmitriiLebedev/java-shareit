package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestControllerTest {

    final MockMvc mockMvc;
    final ObjectMapper objectMapper;
    ItemRequestDto itemRequest;
    User user;
    User user1;
    ItemDto item;
    @MockBean
    private ItemRequestServiceImpl service;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();
        user1 = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane.doe@mail.com")
                .build();
        item = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("thing")
                .available(true)
                .requestId(null)
                .build();
        itemRequest = ItemRequestDto.builder()
                .id(1L)
                .description(item.getDescription())
                .items(new ArrayList<>())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void shouldAddItemRequest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemRequest);
        when(service.create(anyLong(), any())).thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("thing"))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void shouldReturnItemRequestById() throws Exception {
        when(service.getRequestId(anyLong(), anyLong())).thenReturn(itemRequest);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("thing"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void shouldThrowExceptionWhenThereIsNoUsers() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithoutRequest() throws Exception {
        when(service.getByUserId(anyLong())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetItemRequestWithoutPagination() throws Exception {
        when(service.getByUserId(anyLong())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetItemRequestWithPagination() throws Exception {
        when(service.get(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}