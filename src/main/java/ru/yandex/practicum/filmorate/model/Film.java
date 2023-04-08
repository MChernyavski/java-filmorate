package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String name; // название не может быть пустым
    @NotBlank
    @Size(max = 200) //описание должно быть не больше 200 символов
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration; //продолжительность фильма должна быть положительной
}
