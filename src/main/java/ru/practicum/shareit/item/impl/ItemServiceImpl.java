package ru.practicum.shareit.item.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.ShareItUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemModelMapperImpl itemModelMapper;
    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("User not found.");
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
            throw new EntityNotFoundException("Item not found.");
        }

        Item item = itemModelMapper.mapFromDto(itemDto);
        item.setOwner(userRepository.getReferenceById(ownerId));

        ShareItUtils.copyNotNullProperties(item, itemTarget);

        return itemModelMapper.mapToDto(
                repository.save(itemTarget)
        );
    }

    @Override
    public ItemDto get(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Item not found.");
        }

        return itemModelMapper.mapToDto(
                repository.getReferenceById(id)
        );
    }

    @Override
    public List<ItemDto> getByUser(long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("User not found.");
        }

        return repository.getItemsByOwnerId(ownerId)
                .stream()
                .map(itemModelMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()){
            return new ArrayList<>();
        }

        return repository.findContainingText(text)
                .stream()
                .map(itemModelMapper::mapToDto)
                .collect(Collectors.toList());
    }


}
