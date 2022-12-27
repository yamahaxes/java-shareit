package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;

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

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setBookerId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setItemId(1L);

        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();

        when(service.create(any(), anyLong()))
                .thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void create_whenBookingDtoRequestNotValid_thenBadRequest() {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setBookerId(null);
        bookingDtoRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setItemId(1L);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk());

        bookingDtoRequest.setBookerId(1L);
        bookingDtoRequest.setStart(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

        bookingDtoRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoRequest.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

    }

    @SneakyThrows
    @Test
    void create_whenWrongUserId_thenBadRequest() {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setBookerId(null);
        bookingDtoRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1));
        bookingDtoRequest.setItemId(1L);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void approve() {

        BookingDtoResponse response = new BookingDtoResponse();
        when(service.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(response);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(response), result);
    }

    @SneakyThrows
    @Test
    void getById() {

        BookingDtoResponse response = new BookingDtoResponse();
        when(service.getById(anyLong(), anyLong()))
                .thenReturn(response);

        String result = mockMvc.perform(get("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(response), result);
    }

    @SneakyThrows
    @Test
    void getAllBooked() {
        BookingDtoResponse response = new BookingDtoResponse();
        when(service.getAllBooked(anyLong(), anyString(), anyInt(), eq(10)))
                .thenReturn(List.of(response));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(response)), result);
    }

    @SneakyThrows
    @Test
    void getAllBooked_whenFromAndSizeIsEmpty_thenSizeMaxAndFromIsZero() {
        BookingDtoResponse response = new BookingDtoResponse();
        when(service.getAllBooked(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(response));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(response)), result);
        verify(service, times(1)).getAllBooked(anyLong(), anyString(), eq(0), eq(10));
    }


    @SneakyThrows
    @Test
    void getAllByOwner() {

        BookingDtoResponse response = new BookingDtoResponse();
        when(service.getAllByOwner(anyLong(), anyString(), anyInt(), eq(10)))
                .thenReturn(List.of(response));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(response)), result);
    }
}