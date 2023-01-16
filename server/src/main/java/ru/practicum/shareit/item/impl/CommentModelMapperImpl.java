package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.mapper.ModelMapper;

@Component
public class CommentModelMapperImpl implements ModelMapper<Comment, CommentDto> {
    @Override
    public Comment mapFromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        comment.setAuthorName(dto.getAuthorName());
        comment.setCreated(dto.getCreated());

        return comment;
    }

    @Override
    public CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }
}
