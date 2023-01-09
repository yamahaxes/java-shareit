package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponseInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.page.CustomRequestPage;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ShareItUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    private final ModelMapper<Item, ItemDto> itemModelMapper;
    private final ModelMapper<Booking, BookingDtoResponseInfo> bookingMapper;
    private final ModelMapper<Comment, CommentDto> commentMapper;

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        userService.existsUserByUserIdOrThrow(ownerId);

        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwner(userRepository.getReferenceById(ownerId));

        return itemModelMapper.mapToDto(
                repository.save(item)
        );
    }

    @Override
    public ItemDto patch(ItemDto itemDto, long ownerId) {

        Item itemTarget = repository
                .getItemByIdAndOwnerId(itemDto.getId(), ownerId)
                .orElseThrow(NotFoundException::new);

        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwner(userRepository.getReferenceById(ownerId));

        ShareItUtils.copyNotNullProperties(item, itemTarget);

        return itemModelMapper.mapToDto(
                repository.save(itemTarget)
        );
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        existsItemByIdOrThrow(itemId);

        Item item = repository.getReferenceById(itemId);
        ItemDto itemDto = itemModelMapper.mapToDto(item);

        if (userId == item.getOwner().getId()) {
            fillLastNextBooking(List.of(itemDto));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getByUser(long ownerId, int from, int size) {
        userService.existsUserByUserIdOrThrow(ownerId);
        Pageable pageRequest = new CustomRequestPage(from, size);

        return itemListMapToDto(repository.getItemsByOwnerId(ownerId, pageRequest), ownerId);
    }

    @Override
    public List<ItemDto> search(long userId, String text, int from, int size) {
        Pageable pageRequest = new CustomRequestPage(from, size);

        return itemListMapToDto(repository.findContainingText(text, pageRequest), userId);
    }

    @Override
    public CommentDto createComment(long userId, CommentDto commentDto) {
        userService.existsUserByUserIdOrThrow(userId);
        commentDto.setCreated(LocalDateTime.now());

        User author = userRepository.getReferenceById(userId);
        commentDto.setAuthorName(author.getName());

        existsItemByIdOrThrow(commentDto.getItemId());

        long count = bookingRepository.countAllByBooker_IdAndItem_IdAndEndBeforeAndStatus(userId,
                commentDto.getItemId(),
                LocalDateTime.now(),
                BookingStatus.APPROVED);

        if (count == 0) {
            throw new BadRequestException("The user has not rented an item.");
        }

        Item item = repository.getReferenceById(commentDto.getItemId());
        Comment comment = commentMapper.mapFromDto(commentDto);
        comment.setItem(item);

        return commentMapper.mapToDto(
            commentRepository.save(comment)
        );
    }

    private void fillLastNextBooking(List<ItemDto> itemDtoList) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> itemIds = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
        List<Booking> approvedBookings = bookingRepository.getApprovedBookingsByItem_Ids(itemIds);

        itemDtoList.forEach(itemDto -> {
                    Booking nextBooking = approvedBookings.stream()
                            .filter(booking -> booking.getItem().getId() == itemDto.getId()
                                    && booking.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    Booking lastBooking = approvedBookings.stream()
                            .filter(booking -> booking.getItem().getId() == itemDto.getId()
                                    && booking.getStart().isBefore(now))
                            .max(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    itemDto.setNextBooking(bookingMapper.mapToDto(nextBooking));
                    itemDto.setLastBooking(bookingMapper.mapToDto(lastBooking));
        });
    }

    private List<ItemDto> itemListMapToDto(List<Item> items, long userId) {
        List<ItemDto> allItemDto;
        List<ItemDto> itemDtoWithBooking = new ArrayList<>();

        allItemDto = items.stream()
                .map(item -> {
                    ItemDto dto = itemModelMapper.mapToDto(item);
                    if (item.getOwner().getId() == userId) {
                        itemDtoWithBooking.add(dto);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        fillLastNextBooking(itemDtoWithBooking);
        return allItemDto;
    }

    @Override
    public void existsItemByIdOrThrow(long itemId) {
        if (!repository.existsById(itemId)) {
            throw new NotFoundException("Item not found.");
        }
    }
}
