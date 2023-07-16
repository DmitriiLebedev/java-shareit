package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query(value = "SELECT i " +
            "FROM Item AS i " +
            "WHERE (lower(i.name) LIKE %:text% OR lower(i.description) LIKE %:text%) AND i.available=TRUE")
    List<Item> findItemsByTextIgnoreCase(String text);
    List<Item> findAllByRequestIdIn(List<Long> requestsId);
}