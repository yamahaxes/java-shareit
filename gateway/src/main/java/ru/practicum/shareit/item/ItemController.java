package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@AllArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestHeader("X-Sharer-User-Id") long userId,
                         @PathVariable long itemId,
                         @RequestBody ItemDto itemDto) {

        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                   @Positive @RequestParam(required = false, defaultValue = "10") int size) {

        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam String text,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                @Positive @RequestParam(required = false, defaultValue = "10") int size) {

        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }

}
