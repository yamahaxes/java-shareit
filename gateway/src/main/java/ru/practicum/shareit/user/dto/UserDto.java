package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "Incorrect email.")
    private String email;
}
