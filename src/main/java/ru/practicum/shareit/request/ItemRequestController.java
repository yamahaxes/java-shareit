package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDtoResponse create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return service.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getOtherUserRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false) Optional<Integer> size) {

        return service.getOtherUserRequests(userId, from, size.orElse(Integer.MAX_VALUE));
    }

    @GetMapping("/{id}")
    public ItemRequestDtoResponse getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long id) {
        return service.getById(userId, id);
    }
}
