package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "requests", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank
    @Column(name = "description")
    String description;
    @ToString.Exclude
    @Column(name = "user_id")
    Long requester;
    @Column(name = "created")
    LocalDateTime created = LocalDateTime.now();
}
