package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findUserById(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    void removeUser(Long userId);

    User getUserOptional(Long id);
}