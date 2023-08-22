package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    UserService userService;

    UserDto user;

    UserDto user1;

    @BeforeEach
    void setUp() {
        UserDto newUser = UserDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();
        user = userService.create(newUser);
        UserDto newUser1 = UserDto.builder()
                .name("Jane")
                .email("jane.doe@mail.com")
                .build();
        user1 = userService.create(newUser1);
    }

    @Test
    void shouldFindAllUsers() {
        assertThat(userService.findAll(), hasSize(2));
    }

    @Test
    void shouldFindById() {
        assertThat(user.getName(), equalTo(userService.findUserById(1L).getName()));
        assertThat(user.getEmail(), equalTo(userService.findUserById(1L).getEmail()));
    }

    @Test
    void creatingUserWithDuplicateEmailShouldThrowException() {
        UserDto newUser = UserDto.builder()
                .name("bob")
                .email("john.doe@mail.com").build();
        assertThrows(AlreadyExistsException.class,
                () -> userService.create(newUser));
    }

    @Test
    void creatingUserWithoutEmailShouldThrowException() {
        UserDto newUser = UserDto.builder()
                .name("bob").build();
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.create(newUser));
    }

    @Test
    void creatingUserWithIncorrectEmailShouldThrowException() {
        UserDto newUser = UserDto.builder()
                .name("bob")
                .email("not.email").build();
        assertThrows(ValidationException.class,
                () -> userService.create(newUser));
    }

    @Test
    void shouldUpdateUser() {
        UserDto newUser = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();
        UserDto updatedUser = userService.update(newUser);
        assertThat(updatedUser.getName(), is("John Doe"));
        assertThat(updatedUser.getEmail(), is("john.doe@mail.com"));
    }

    @Test
    void updatingNotExistingUserShouldThrowException() {
        UserDto newUser = UserDto.builder()
                .id(99L)
                .name("bob")
                .email("bob.lee@mail.com")
                .build();
        assertThrows(NotFoundException.class,
                () -> userService.update(newUser));
    }

    @Test
    void shouldRemoveAllUsers() {
        userService.removeUser(1L);
        assertThat(userService.findAll(), hasSize(1));
        assertThat(userService.findAll().get(0).getId(), is(user1.getId()));
    }
}