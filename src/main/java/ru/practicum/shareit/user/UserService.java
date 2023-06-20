package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserStorage userStorage;
    UserMapper userMapper;

    public List<UserDto> findAll() {
        return userStorage.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        return userMapper.toUserDto(userStorage.findUserById(id));
    }

    public UserDto create(UserDto userDto) {
        checkEmail(userDto.getEmail());
        User user = userStorage.createUser(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto) {
        User user = userStorage.findUserById(userDto.getId());
        String email = userDto.getEmail();
        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
        }
        if (email != null && !email.equals(user.getEmail())) {
            checkEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        user = userStorage.updateUser(user);
        return userMapper.toUserDto(user);
    }

    public void removeUser(Long id) {
        userStorage.removeUser(id);
    }

    public void checkEmail(String email) {
        if (email == null) {
            throw new ValidationException("Email can't be empty");
        }
        if (userStorage.findAll().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new AlreadyExistsException("Email address already in use");
        }
    }
}