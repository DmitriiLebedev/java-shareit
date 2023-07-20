package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();
        item = Item.builder()
                .name("Stuff")
                .description("desc")
                .available(true)
                .ownerId(1L)
                .build();
    }

    @Test
    void shouldSearchAndFindItemByIncompleteNameIgnoringCase() {
        userRepository.save(user);
        itemRepository.save(item);
        List<Item> itemList = itemRepository.findItemsByTextIgnoreCase("stu");
        assertEquals(itemList.size(), 1);
    }
}