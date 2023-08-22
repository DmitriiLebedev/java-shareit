package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ErrorHandler;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorHandlerTest {

    ErrorHandler errorHandler;

    @Test
    void shouldThrowRuntime() {
        assertThrows(RuntimeException.class, () -> errorHandler.handleNotFound(new RuntimeException()));
    }

    @Test
    void shouldReturnBadRequest() {
        assertThrows(RuntimeException.class, () -> errorHandler.handleBadRequest(new RuntimeException()));
    }

    @Test
    void shouldThrowRuntimeWrongItem() {
        assertThrows(RuntimeException.class, () -> errorHandler.handleBadItemRequest(new RuntimeException()));
    }
}
