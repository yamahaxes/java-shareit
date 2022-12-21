package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.impl.ItemRequestServiceImpl;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @InjectMocks
    private ItemRequestController controller;

    @Mock
    private ItemRequestServiceImpl service;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        dateTime = LocalDateTime.now();
    }

    @Test
    void create() throws Exception {
        /*
        * when(userService.saveUser(any()))
            .thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
            .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        * */


        ItemRequestDtoResponse itemRequestDtoResponse = makeItemRequestDtoResponse(1);
        when(service.createItemRequest(anyInt(), any()))
                .thenReturn(itemRequestDtoResponse);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("itemRequest1");

        mvc.perform(
                post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath( "$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()))
                .andExpect(jsonPath("$.created").value(dateTime.plusMinutes(1)));

    }

    @Test
    void getUserRequests() {
    }

    @Test
    void getOtherUserRequests() {
    }

    @Test
    void getById() {
    }

    private ItemRequestDtoResponse makeItemRequestDtoResponse(int i) {

        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(i);
        itemRequestDtoResponse.setDescription("itemRequest" + i);
        itemRequestDtoResponse.setCreated(dateTime.plusMinutes(i));
        itemRequestDtoResponse.setItems(new ArrayList<>());

        return itemRequestDtoResponse;
    }
}