package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponseInfo;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private List<CommentDto> comments = new ArrayList<>();

    private BookingDtoResponseInfo lastBooking;

    private BookingDtoResponseInfo nextBooking;

    private Long requestId;
}
