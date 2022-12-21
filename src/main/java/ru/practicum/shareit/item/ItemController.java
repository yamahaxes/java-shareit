package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") long userId,
                         @PathVariable long itemId,
                         @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.patch(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam(required = false) Optional<Integer> from,
                                   @RequestParam(required = false) Optional<Integer> size) {

        if (from.isEmpty() && size.isEmpty()) {
            return itemService.getByUser(userId, 0, Integer.MAX_VALUE);
        } else if (from.isEmpty() || size.isEmpty()) {
            throw new BadRequestException();
        }

        return itemService.getByUser(userId, from.get(), size.get());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam String text,
                                @RequestParam(required = false) Optional<Integer> from,
                                @RequestParam(required = false) Optional<Integer> size) {

        if (from.isEmpty() && size.isEmpty()) {
            return itemService.search(userId, text.toLowerCase(), 0, Integer.MAX_VALUE);
        } else if (from.isEmpty() || size.isEmpty()) {
            throw new BadRequestException();
        }

        return itemService.search(userId, text.toLowerCase(), from.get(), size.get());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        commentDto.setItemId(itemId);
        return itemService.createComment(userId, commentDto);
    }

}
