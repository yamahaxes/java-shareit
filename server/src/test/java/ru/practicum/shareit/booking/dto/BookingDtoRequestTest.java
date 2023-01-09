package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoRequestTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @SneakyThrows
    @Test
    void testBookingDtoRequest() {

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setBookerId(2L);
        bookingDtoRequest.setStart(LocalDateTime.now());
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(1));

        JsonContent<BookingDtoRequest> result = json.write(bookingDtoRequest);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDtoRequest.getStart().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDtoRequest.getEnd().format(formatter));

    }


}