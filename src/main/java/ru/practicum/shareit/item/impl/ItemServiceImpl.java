package ru.practicum.shareit.item.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemModelMapperImpl itemModelMapper;
    private final ItemRepository repository;

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwnerId(ownerId);

        return itemModelMapper.mapToDto(
                repository.create(item)
        );
    }

    @Override
    public ItemDto patch(ItemDto itemDto, long ownerId) {
        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwnerId(ownerId);

        return itemModelMapper.mapToDto(
                repository.patch(item)
        );
    }

    @Override
    public ItemDto get(long id) {
        return itemModelMapper.mapToDto(repository.get(id));
    }

    @Override
    public List<ItemDto> getByUser(long ownerId) {
        return repository.getItemsByUser(ownerId)
                .stream()
                .map(itemModelMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return repository.search(text)
                .stream()
                .map(itemModelMapper::mapToDto)
                .collect(Collectors.toList());
    }


}
