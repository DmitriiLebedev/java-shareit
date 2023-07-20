package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.debug("GET request for Users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") @Positive Long id) {
        log.debug("GET request for User id " + id);
        return userService.findUserById(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("POST request for User");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("id") @Positive Long id) {
        userDto.setId(id);
        log.debug("PATCH request for User id " + id);
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        log.debug("DELETE request for User id " + id);
        userService.removeUser(id);
    }
}