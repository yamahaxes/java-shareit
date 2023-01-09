package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        return userService.get(id);
    }

    @PatchMapping("/{id}")
    public UserDto patch(@RequestBody UserDto userDto,
                         @PathVariable long id) {
        userDto.setId(id);
        return userService.patch(userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }
}
