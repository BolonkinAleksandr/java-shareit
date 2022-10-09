package ru.practicum.shareit.user.model;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class User {
    private long id;
    private String name;
    private String email;
}
