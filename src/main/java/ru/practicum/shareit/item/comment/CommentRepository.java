package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select new ru.practicum.shareit.item.comment.CommentDto" +
            "(c.id, c.text, c.author.name, c.created) " +
            "from Comment as c " +
            "where c.item.id = ?1")
    List<CommentDto> findAllByItem(Long itemId);
}
