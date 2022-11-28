package ru.practicum.shareit.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    private String name;
    /*@NotNull*/
    @Email
    private String email;
}
