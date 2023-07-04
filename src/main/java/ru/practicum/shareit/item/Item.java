package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "description")
    String description;
    @Column(name = "available")
    Boolean available;
    @Column(name = "owner_id")
    Long ownerId;
    @Column(name = "request_id")
    Long requestId;
}
