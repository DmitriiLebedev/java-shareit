package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStorageImpl implements UserStorage {

    final Map<Long, User> users = new HashMap<>();
    Long id = 0L;

    private Long increaseId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
        user.setId(increaseId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long id) {
        checkId(id);
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        checkId(id);
        user.setId(id);
        users.replace(id, user);
        return user;
    }

    @Override
    public void removeUser(Long id) {
        checkId(id);
        users.remove(id);
    }

    private void checkId(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Can't find user id:" + id);
        }
    }

}