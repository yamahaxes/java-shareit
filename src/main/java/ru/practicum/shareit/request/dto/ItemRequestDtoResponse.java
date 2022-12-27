package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDtoResponse {

    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;

}
