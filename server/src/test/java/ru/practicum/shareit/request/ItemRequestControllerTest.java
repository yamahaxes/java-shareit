package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @SneakyThrows
    @Test
    void create() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("name");

        ItemRequestDtoResponse response = new ItemRequestDtoResponse();

        when(service.createItemRequest(anyLong(), any()))
                .thenReturn(response);

        String result = mockMvc.perform(post("/requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequestDto))
                    .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(response), result);
    }

    @SneakyThrows
    @Test
    void create_whenItemRequestNotValid_thenBadRequest() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

    }

    @SneakyThrows
    @Test
    void getUserRequests() {

        ItemRequestDtoResponse response = new ItemRequestDtoResponse();
        response.setId(10L);

        when(service.getUserRequests(anyLong()))
                .thenReturn(List.of(response));

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(response)), result);
    }

    @SneakyThrows
    @Test
    void getOtherUserRequestsWithPagination() {

        ItemRequestDtoResponse response = new ItemRequestDtoResponse();
        response.setId(10L);

        when(service.getOtherUserRequests(anyLong(),anyInt(), anyInt()))
                .thenReturn(List.of(response));

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(response)), result);

    }

    @SneakyThrows
    @Test
    void getById() {

        ItemRequestDtoResponse response = new ItemRequestDtoResponse();
        response.setId(10L);

        when(service.getById(anyLong(), anyLong()))
                .thenReturn(response);

        String result = mockMvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(response), result);

    }
}