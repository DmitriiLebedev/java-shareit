package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.toUserDto(getUserOptional(id));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        String email = userDto.getEmail();
        if (checkEmail(email)) {
            throw new AlreadyExistsException("Email address already in use");
        }
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {
        User user = getUserOptional(userDto.getId());
        String email = userDto.getEmail();
        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
        }
        if (checkEmail(email) && !email.equals(user.getEmail())) {
            throw new AlreadyExistsException("Email address already in use");
        }
        if (email != null) {
            user.setEmail(email);
        }
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(Long id) {
        getUserOptional(id);
        userRepository.deleteById(id);
    }

    private boolean checkEmail(String email) {
        return userRepository.findAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    @Override
    public User getUserOptional(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find user id:" + id));
    }
}