package ru.practicum.shareit.item.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponseInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.util.ShareItUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ModelMapper<Item, ItemDto> itemModelMapper;
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper<Booking, BookingDtoResponseInfo> bookingMapper;

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException();
        }

        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwner(userRepository.getReferenceById(ownerId));

        return itemModelMapper.mapToDto(
                repository.save(item)
        );
    }

    @Override
    public ItemDto patch(ItemDto itemDto, long ownerId) {

        Item itemTarget = repository.getItemByIdAndOwnerId(itemDto.getId(), ownerId);
        if (itemTarget == null){
            throw new ItemNotFoundException();
        }

        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwner(userRepository.getReferenceById(ownerId));

        ShareItUtils.copyNotNullProperties(item, itemTarget);

        return itemModelMapper.mapToDto(
                repository.save(itemTarget)
        );
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        if (!repository.existsById(itemId)) {
            throw new ItemNotFoundException();
        }

        Item item = repository.getReferenceById(itemId);
        ItemDto itemDto = itemModelMapper.mapToDto(item);

        if (userId == item.getOwner().getId()) {
            fillLastNextBooking(itemDto);
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getByUser(long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException();
        }

        return itemListMapToDto(repository.getItemsByOwnerId(ownerId), ownerId);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        if (text.isBlank()){
            return new ArrayList<>();
        }

        return itemListMapToDto(repository.findContainingText(text), userId);
    }

    private void fillLastNextBooking(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();

        BookingDtoResponseInfo lastBooking = bookingMapper.mapToDto(
                bookingRepository.getFirstByItem_IdAndEndBeforeOrderByEndDesc(itemDto.getId(), now)
        );

        BookingDtoResponseInfo nextBooking = bookingMapper.mapToDto(
                bookingRepository.getFirstByItem_IdAndStartAfterOrderByStartAsc(itemDto.getId(), now)
        );

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
    }

    private List<ItemDto> itemListMapToDto(List<Item> items, long userId) {
        return items.stream()
                .map(item -> {
                    ItemDto dto = itemModelMapper.mapToDto(item);
                    if (item.getOwner().getId() == userId) {
                        fillLastNextBooking(dto);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
