package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDtoResponse create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return service.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getOtherUserRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "0") int size) {

        return service.getOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDtoResponse getById(@PathVariable long id) {
        return service.getById(id);
    }
}
