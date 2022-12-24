package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void testItemDto() {

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("name");
        itemDto.setAvailable(true);
        itemDto.setDescription("name desc");
        itemDto.setRequestId(1L);
        itemDto.setComments(new ArrayList<>());

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(new ArrayList<CommentDto>());

    }

}