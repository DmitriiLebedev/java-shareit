package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    final User booker = new User();
    final User owner = new User();
    final Item item = new Item();
    final ItemBookingModel itemBookingModel = new ItemBookingModel();
    final List<CommentDto> comments = new ArrayList<>();
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    ItemDto itemDto;
    CommentDto commentDto;
    @MockBean
    private ItemServiceImpl itemService;

    @BeforeEach
    public void setUp() {

        itemDto = new ItemDto(1L, "item", "desc", true, null);
        commentDto = new CommentDto(1L, "text", "John", LocalDateTime
                .of(2020, Month.JANUARY, 1, 1, 0, 0));
        owner.setId(1L);
        owner.setName("Jane");
        owner.setEmail("jane.doe@mail.com");
        booker.setId(2L);
        booker.setName("John");
        booker.setEmail("john.doe@mail.com");
        item.setId(1L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        itemBookingModel.setId(1L);
        itemBookingModel.setName("item");
        itemBookingModel.setDescription("desc");
        itemBookingModel.setAvailable(true);
        itemBookingModel.setComments(comments);
    }

    @Test
    public void shouldCreateItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        String json = mapper.writeValueAsString(itemDto);
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);
        itemDto.setName("updated");
        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    public void shouldFindItemById() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(itemBookingModel);
        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.comments", is(itemBookingModel.getComments())));
    }

    @Test
    public void shouldFindAllItems() throws Exception {
        when(itemService.findAllItemsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemBookingModel));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].comments", is(itemBookingModel.getComments())));
    }

    @Test
    public void shouldSearchForItems() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search?text=item")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    public void shouldAddCommentToItem() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created").value("2020-01-01T01:00:00"));
    }
}