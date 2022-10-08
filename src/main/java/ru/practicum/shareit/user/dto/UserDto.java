package ru.practicum.shareit.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    private String name;
    @Email
    @NotNull
    private String email;
}
