package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> findAll();

    User findUserById(Long id);

    void removeUser(Long id);
}