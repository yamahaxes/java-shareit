package ru.practicum.shareit.item.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentModelMapperImplTest {

    @InjectMocks
    private CommentModelMapperImpl mapper;

    @Test
    void mapFromDto() {

        assertDoesNotThrow(() -> mapper.mapFromDto(new CommentDto()));

    }

    @Test
    void mapToDto() {
        Item item = new Item();
        item.setId(2L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);

        assertDoesNotThrow(() -> mapper.mapToDto(comment));

    }
}